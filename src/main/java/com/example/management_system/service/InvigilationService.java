package com.example.management_system.service;

import com.example.management_system.entity.Exam;
import com.example.management_system.entity.Invigilation;
import com.example.management_system.entity.User;
import com.example.management_system.repository.InvigilationRepository;
import com.example.management_system.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InvigilationService {
    @Autowired
    private InvigilationRepository ir;
    @Autowired
    private UserService us;
    @Autowired
    private UserRepository ur;

    public List<Invigilation> findByTeacher(int tid) {
        return ir.listByTeacher(tid);
    }

    /**
     * maybe throw exception NotFoundException --- listByTeacher(), to be fixed
     *
     * @param invigilation
     */
    public void assign(Invigilation invigilation) {
        Exam exam = invigilation.getExam();
        LocalDateTime startTime = exam.getStartTime();
        int classroom = exam.getClassroom();
        //所有教师信息列表
        List<User> users = us.findAllUser();
        //可分派监考任务的教师列表，初始为空
        List<User> userAvailable = null;
        //bestUsers 监考次数为0的教师列表
        List<User> bestUsers = users.stream()
                .filter(u -> u.getFrequency() == 0)
                .collect(Collectors.toList());
        //secondUsers 监考次数全不为0的教师列表
        List<User> secondUsers = users.stream()
                .filter(u -> u.getFrequency() != 0)
                .collect(Collectors.toList());
        if (bestUsers != null) { //若监考次数为0，则直接分配监考任务
            User u = bestUsers.get(0);
            invigilation.setTeacher(u);
            u.setFrequency(1);
            us.updateInformation(u);
        } else {
            for (User u : secondUsers) {//保证userAvailable没有监考次数为0，且合要求的教师
                List<Invigilation> invigilations = ir.listByTeacher(u.getId());
                for (Invigilation i : invigilations) {
                    if (i.getExam().getStartTime() != startTime || i.getExam().getClassroom() != classroom) {
                        userAvailable.add(u);
                    }
                }
            }
            int times = 50;
            //找出最少监考次数
            for (User u : userAvailable) {
                if (u.getFrequency() < times) {
                    times = u.getFrequency();
                }
            }
            for (User u : userAvailable) {
                if (u.getFrequency() == times) {
                    //若同一时间已安排其他考试，则发出冲突提示
                    List<Invigilation> invigilations = ir.listByTeacher(u.getId());
                    for (Invigilation i : invigilations) {
                        if (i.getExam().getStartTime() == startTime) {
                            log.debug("请注意，同一时间安排了两门考试");
                        }
                    }
                    //给监考次数最少的教师分配监考任务
                    invigilation.setTeacher(u);
                    u.setFrequency(++times);
                    us.updateInformation(u);
                }
            }
        }

    }
}

