package com.lambdaschool.dogsinitial.Services;

import com.lambdaschool.dogsinitial.DogsinitialApplication;
import com.lambdaschool.dogsinitial.model.MessageDetail;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageListener
{
    @RabbitListener(queues = DogsinitialApplication.QUEUE_NAME_HIGH)
    public void receiveMessage(MessageDetail msg)
    {
        System.out.println("Received High Queue message {" + msg.toString() + "}");
    }

    @RabbitListener(queues = DogsinitialApplication.QUEUE_NAME_LOW)
    public void receiveLowMessage(MessageDetail msg)
    {
        System.out.println("Receive Low Queue message {" + msg.toString() + "}");
    }
}
