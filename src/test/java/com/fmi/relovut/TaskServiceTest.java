package com.fmi.relovut;

import com.fmi.relovut.dto.MemberDto;
import com.fmi.relovut.dto.tasks.CreateTaskDto;
import com.fmi.relovut.models.Assignee;
import com.fmi.relovut.models.AssigneeMember;
import com.fmi.relovut.models.Task;
import com.fmi.relovut.repositories.AssigneeMemberRepository;
import com.fmi.relovut.repositories.AssigneeRepository;
import com.fmi.relovut.repositories.TaskRepository;
import com.fmi.relovut.services.TaskService;
import com.fmi.relovut.services.UserGroupService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.security.Principal;
import java.util.Set;

import static com.fmi.relovut.dto.tasks.CreateTaskDto.createTaskDtoBuilder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TaskServiceTest {
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

    @Mock
    private Principal principal;

    @Captor
    private ArgumentCaptor<Task> taskArgumentCaptor;
    @Captor
    private ArgumentCaptor<Set<AssigneeMember>> assigneeMemberArgumentCaptor;


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        taskService = new TaskService(taskRepository, userGroupService, assigneeRepository, assigneeMemberRepository, null, null);
    }

    @Test(expected = IllegalAccessException.class)
    public void create_task_with_no_assignees(){
        CreateTaskDto taskDto = buildTask(null, null, null);

        when(principal.getName()).thenReturn("email");

        taskService.createOrEditTask(taskDto, principal);
    }

    @Test
    public void create_task(){
        CreateTaskDto taskDto = buildTask(null, Set.of(new MemberDto(2L)), null);

        when(principal.getName()).thenReturn("email");
        when(taskRepository.save(any())).thenReturn(buildTaskEntity());
        when(assigneeRepository.save(any())).thenReturn(Assignee.builder()
                .id(3L)
                .build());

        taskService.createOrEditTask(taskDto, principal);

        Mockito.verify(this.taskRepository,
                Mockito.times(1)).save(taskArgumentCaptor.capture());
        Task task = taskArgumentCaptor.getValue();

        Mockito.verify(this.assigneeMemberRepository,
                Mockito.times(1)).saveAll(assigneeMemberArgumentCaptor.capture());
        AssigneeMember assigneeMember = assigneeMemberArgumentCaptor.getValue().iterator().next();

        Assert.assertEquals(Task.Status.IN_PROGRESS, task.getStatus());
        Assert.assertEquals(taskDto.getGroups().iterator().next().getId(), assigneeMember.getGroupId());

    }

    @Test(expected = IllegalAccessException.class)
    public void update_task_with_no_manager(){
        CreateTaskDto taskDto = buildTask(1L, Set.of(new MemberDto(2L)), null);

        when(principal.getName()).thenReturn("email");
        when(taskRepository.findByTaskIdEagerlyActive(any()))
                .thenReturn(java.util.Optional.of(new Task()
                        .setActiveAssignedUsers(Set.of(new Assignee()
                        .setActiveAssigneeMemberSet(Set.of(new AssigneeMember().setGroupId(5L)))))));

        taskService.createOrEditTask(taskDto, principal);

        Mockito.verify(this.taskRepository,
                Mockito.times(1)).save(taskArgumentCaptor.capture());
        Task task = taskArgumentCaptor.getValue();

        Assert.assertEquals(taskDto.getCategory(), task.getCategory());
        Assert.assertEquals(Task.Status.IN_PROGRESS, task.getStatus());
        Assert.assertEquals(taskDto.getTitle(), task.getTitle());
        Assert.assertEquals(taskDto.getContent(), task.getContent());
    }

    public CreateTaskDto buildTask(Long id, Set<MemberDto> groups, Set<MemberDto> users){
        return createTaskDtoBuilder()
                .category(Task.Category.Cleaning)
                .title("title")
                .content("content")
                .users(users)
                .groups(groups)
                .id(id)
                .build();
    }

    public Task buildTaskEntity(){
        return Task.builder()
                .id(1L)
                .category(Task.Category.Cleaning)
                .title("title")
                .content("content")
                .status(Task.Status.IN_PROGRESS)
                .build();
    }
}
