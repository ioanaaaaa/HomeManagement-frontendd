package com.fmi.relovut;

import com.fmi.relovut.models.Assignee;
import com.fmi.relovut.models.AssigneeMember;
import com.fmi.relovut.models.Task;
import com.fmi.relovut.models.User;
import com.fmi.relovut.repositories.AssigneeMemberRepository;
import com.fmi.relovut.repositories.AssigneeRepository;
import com.fmi.relovut.repositories.TaskRepository;
import com.fmi.relovut.repositories.UserRepository;
import com.fmi.relovut.services.TaskService;
import com.fmi.relovut.services.UserGroupService;
import com.fmi.relovut.services.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TaskServiceShould {
    @InjectMocks
    private TaskService taskService;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private AssigneeRepository assigneeRepository;
    @Mock
    private AssigneeMemberRepository assigneeMemberRepository;
    @InjectMocks
    private UserGroupService userGroupService;
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Mock
    private Principal principal;

    @Captor
    private ArgumentCaptor<Task> taskArgumentCaptor;
    @Captor
    private ArgumentCaptor<Set<AssigneeMember>> assigneeMemberArgumentCaptor;


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(null, userRepository);
        taskService = new TaskService(taskRepository, userGroupService, assigneeRepository, assigneeMemberRepository, userService, null);
    }

    /**
     * Task has user A and group B (that has  user A as a member) as assignees
     */
    @Test
    public void claim_task_with_user_and_group(){
        when(principal.getName()).thenReturn("email");
        when(userRepository.findByEmail(any())).thenReturn(User.builder()
                .id(2L)
                .build());

        when(taskRepository.findByTaskIdEagerlyActive(any())).thenReturn(buildTask());

        taskService.claimTask(principal, 1L);

        Mockito.verify(this.assigneeMemberRepository,
                Mockito.times(1)).saveAll(assigneeMemberArgumentCaptor.capture());
        Set<AssigneeMember> assigneeMembers = assigneeMemberArgumentCaptor.getValue();

        Assert.assertEquals(2, assigneeMembers.size());

        //check if user assigneeMember is still active
        Assert.assertEquals(Long.valueOf(2L), assigneeMembers.stream()
                .filter(AssigneeMember::isActive)
                .map(AssigneeMember::getUserId).collect(toList()).get(0));

        //check if group assignee member is deactivated
        assigneeMembers.remove(assigneeMembers.stream()
                .filter(AssigneeMember::isActive)
                .map(AssigneeMember::getUserId).collect(toList()).get(0));
        Assert.assertFalse(assigneeMembers.iterator().next().isActive());

        Mockito.verify(this.taskRepository,
                Mockito.times(1)).save(taskArgumentCaptor.capture());
        Task task = taskArgumentCaptor.getValue();

        //check for claimed by principal
        Assert.assertEquals(Long.valueOf(2L), task.getClaimedBy());
    }

    public Optional<Task> buildTask(){
        Set<AssigneeMember> assigneeMemberSet = new HashSet<>();
        assigneeMemberSet.add(buildAssigneeMember(2L, null, null));
        assigneeMemberSet.add(buildAssigneeMember(null, 3L, null));

        return Optional.of(Task.builder()
                .id(1L)
                .activeAssignedUsers(Set.of(Assignee.builder()
                        .activeAssigneeMemberSet(assigneeMemberSet)
                        .build()))
                .status(Task.Status.IN_PROGRESS)
                .build()
        );
    }

    public AssigneeMember buildAssigneeMember(Long userId, Long groupId, Long assigneeId){
        return AssigneeMember.builder()
                .userId(userId)
                .groupId(groupId)
                .active(true)
                .assigneeId(assigneeId)
                .build();
    }


}
