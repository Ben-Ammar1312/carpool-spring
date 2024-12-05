package gle.carpoolspring.service;

import gle.carpoolspring.model.LinkedUser;
import gle.carpoolspring.model.Message;
import gle.carpoolspring.repository.LinkedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LinkedUserService {

    @Autowired
    private LinkedUserRepository linkeduserRepository;


    public List<Integer> getLinkedUsersIds(int senderId){

        return linkeduserRepository.findLinkedUserIdsByUserId(senderId);

    }


}
