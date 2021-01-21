package com.fmi.relovut.services;

import com.fmi.relovut.dto.GroupDto;
import com.fmi.relovut.dto.MemberDto;
import com.fmi.relovut.dto.tasks.CreateTaskDto;
import com.fmi.relovut.dto.tasks.TaskModelDto;
import com.fmi.relovut.dto.user.UserDto;
import com.fmi.relovut.models.*;
import com.fmi.relovut.repositories.AssigneeMemberRepository;
import com.fmi.relovut.repositories.AssigneeRepository;
import com.fmi.relovut.repositories.TaskRepository;
import javassist.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserGroupService userGroupService;
    private final AssigneeRepository assigneeRepository;
    private final AssigneeMemberRepository assigneeMemberRepository;
    private final UserService userService;
    private final GroupService groupService;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserGroupService userGroupService, AssigneeRepository assigneeRepository, AssigneeMemberRepository assigneeMemberRepository, UserService userService, GroupService groupService) {
        this.taskRepository = taskRepository;
        this.userGroupService = userGroupService;
        this.assigneeRepository = assigneeRepository;
        this.assigneeMemberRepository = assigneeMemberRepository;
        this.userService = userService;
        this.groupService = groupService;
    }

    /**
     * Get open and completed tasks for members of the groups that are managed by principle
     * @param principal
     * @return
     */
    @Transactional(readOnly = true)
    public List<TaskModelDto> getMyTeamsTasks(Principal principal){
        //get user
        User user = userService.getByEmail(principal.getName());

        Set<Long> groupIds = userGroupService.getGroupIdsManagedByPrinciple(user.getId());

        if(CollectionUtils.isEmpty(groupIds))
            return null;

        List<Task> tasks = taskRepository.findByActiveAssignedUsers_activeAssigneeMemberSet_groupIdIn(groupIds);

        List<TaskModelDto> taskModelDtos = new ArrayList<>();
        for (Task task : tasks) {
            TaskModelDto taskModelDto = convertToModel(task);

            taskModelDtos.add(taskModelDto);
        }

        return taskModelDtos;

    }

    /**
     * Deletes the specified task only if the current user is manager
     * @param id
     */
    @Transactional
    public void deleteTask(Long id, Principal principal) throws IllegalAccessException, NotFoundException {
        if(null == id){
            throw new IllegalAccessException("The task with id" + id + " does not exist!");
        }

        Optional<Task> taskOptional = taskRepository.findById(id);
        if(!taskOptional.isPresent()){
            throw new NotFoundException("The task with id " + id + " was not found!");
        }

        //check it the principal is manager in order to delete the task
        if (Boolean.FALSE.equals(checkForManager(taskOptional.get(), principal.getName()))) {
            throw new IllegalAccessException("You can not delete this task because you are not a manager");
        }

        Set<Assignee> assignees = taskOptional.get().getAssignedUsers();
        Set<Long> assigneeIds = assignees.stream().map(Assignee::getId).collect(toSet());
        Set<Long> assigneeMembers = assignees.stream().map(Assignee::getAssigneeMemberSet)
                .flatMap(list -> list.stream())
                .map(AssigneeMember::getId)
                .collect(Collectors.toSet());

        assigneeMemberRepository.deleteAllByIdIn(assigneeMembers);
        assigneeRepository.deleteAllByIdIn(assigneeIds);
        taskRepository.delete(taskOptional.get());

    }

    //la reassign task trebuie sa setez si claim by cu null;
//    public void reassignTask(){
//
//    }

    @Transactional
    public void submitTask(Principal principal, Long taskId) throws IllegalAccessException, NotFoundException {
        if(null == taskId){
            throw new IllegalAccessException("You must provide a task id in order to submit the task!");
        }

        //get user
        User user = userService.getByEmail(principal.getName());

        Optional<Task> optionalTask = taskRepository.findByTaskIdEagerlyActive(taskId);
        if(!optionalTask.isPresent()){
            throw new NotFoundException("The task with id " + taskId + " was not found!");
        }

        Task task = optionalTask.get();
        if(Task.Status.COMPLETED.name().equalsIgnoreCase(task.getStatus().name())){
            throw new IllegalAccessException("The task was completed!");
        }

        if(null == task.getClaimedBy() || null == task.getClaimedByUser()){
            throw new IllegalAccessException("The task was not claimed!");
        }

        if(!user.getId().equals(task.getClaimedBy())){
            throw new IllegalAccessException("Tou did not claimed this task!");
        }

        task.setStatus(Task.Status.COMPLETED);

        taskRepository.save(task);
    }

    @SneakyThrows
    @Transactional
    public void claimTask(Principal principal, Long taskId){
        if(null == taskId){
            throw new IllegalAccessException("You must provide a task id in order to claim the task!");
        }

        //get user
        User user = userService.getByEmail(principal.getName());

        Optional<Task> optionalTask = taskRepository.findByTaskIdEagerlyActive(taskId);
        if(!optionalTask.isPresent()){
            throw new NotFoundException("The task with id " + taskId + " was not found!");
        }

        Task task = optionalTask.get();
        if(Task.Status.COMPLETED.name().equalsIgnoreCase(task.getStatus().name())){
            throw new IllegalAccessException("The task was completed!");
        }

        if(null != task.getClaimedBy() || null != task.getClaimedByUser()){
            throw new IllegalAccessException("The task was already claimed!");
        }

        task.setClaimedBy(user.getId());

        Set<Assignee> taskAssignees = task.getActiveAssignedUsers();
        if(taskAssignees.size() > 1){
            throw new IllegalAccessException("The task has more than 1 assignee!");
        }

        AssigneeMember foundAssigneeMember = null;
        for(AssigneeMember assigneeMember : taskAssignees.iterator().next().getActiveAssigneeMemberSet()){
            //we can have use A as assignee and group that contains user A so we deactivate the group
            if(user.getId().equals(assigneeMember.getUserId()) && null == foundAssigneeMember){
                foundAssigneeMember = assigneeMember;
            } else if(null != foundAssigneeMember || !user.getId().equals(assigneeMember.getUserId())){
                assigneeMember.setActive(false);
                continue;
            }

            if(null != assigneeMember.getGroupId() && null != foundAssigneeMember){
                Set<Long> userIds = userGroupService.getUserIdsForGroup(assigneeMember.getGroupId());
                if(userIds.contains(user.getId())){
                    foundAssigneeMember = assigneeMember;
                } else {
                    assigneeMember.setActive(false);
                }
            }
        }

        assigneeMemberRepository.saveAll(taskAssignees.iterator().next().getActiveAssigneeMemberSet());
        taskRepository.save(task);
    }

    @Transactional
    public Set<TaskModelDto> getAllTasks() {
        Set<Task> tasks = taskRepository.findAll();

        Set<TaskModelDto> taskModelDtos = new HashSet<>();
        for (Task task : tasks) {
            TaskModelDto taskModelDto = convertToModel(task);

            taskModelDtos.add(taskModelDto);
        }

        return taskModelDtos;
    }

    /**
     * Get open tasks for current user.
     * returns also the tasks that are assigned to groups to which the user belongs.
     * @param principal
     * @return Set<TaskModelDto>
     */
    @Transactional
    public List<TaskModelDto> getOpenTasksForCurrentUser(Principal principal) {
        //get user
        User user = userService.getByEmail(principal.getName());
        //get groups for current user
        Set<Long> groupIds = userGroupService.getGroupsIdsForUser(user.getId());

        Set<Task> tasks = taskRepository.findAllActiveForUser(user.getId(), groupIds);

        List<TaskModelDto> taskModelDtos = new ArrayList<>();
        for (Task task : tasks) {
            TaskModelDto taskModelDto = convertToModel(task);

            taskModelDtos.add(taskModelDto);
        }

        return taskModelDtos;
    }

    /**
     * Get completed tasks for current user.
     *
     * @param principal
     * @return Set<TaskModelDto>
     */
    @Transactional
    public List<TaskModelDto> getCompletedTasksForCurrentUser(Principal principal) {
        //get user
        User user = userService.getByEmail(principal.getName());

        Set<Task> tasks = taskRepository.findAllCompletedForUser(user.getId());

        List<TaskModelDto> taskModelDtos = new ArrayList<>();
        for (Task task : tasks) {
            TaskModelDto taskModelDto = convertToModel(task);

            taskModelDtos.add(taskModelDto);
        }

        return taskModelDtos;
    }

    @Transactional(readOnly = true)
    public TaskModelDto convertToModel(Task task) {
        TaskModelDto taskModelDto = new TaskModelDto(task);

        //if the task was not claimed yet then get all the assignees
        if(null == task.getClaimedBy()) {
            Set<AssigneeMember> assigneeMembers = task.getActiveAssignedUsers().iterator().next().getActiveAssigneeMemberSet();
            Set<Long> userIds = assigneeMembers.stream().map(AssigneeMember::getUserId).collect(Collectors.toSet());
            Set<Long> groupIds = assigneeMembers.stream().map(AssigneeMember::getGroupId).collect(Collectors.toSet());

            if (!CollectionUtils.isEmpty(userIds)) {
                Map<User, Boolean> users = new HashMap<>();
                userService.findUsersByIds(userIds).forEach(user -> {
                    users.put(user, false);
                });

                taskModelDto.setUsers(UserDto.toDtos(users));
            }

            if (!CollectionUtils.isEmpty(groupIds)) {
                List<Group> groups = new ArrayList<>(groupService.findGroupsByIds(groupIds));

                taskModelDto.setGroups(GroupDto.toDtos(groups));
            }
        }

        return taskModelDto;
    }

    @SneakyThrows
    @Transactional
    public void createOrEditTask(CreateTaskDto taskDto, Principal principal) {
        if (null == taskDto.getId()) {// create
            if (CollectionUtils.isEmpty(taskDto.getGroups()) && CollectionUtils.isEmpty(taskDto.getUsers())) {
                throw new IllegalAccessException("You do not have users or groups assigned to this task!");
            }

            Task task = new Task();
            task.setContent(taskDto.getContent());
            task.setTitle(taskDto.getTitle());
            task.setStatus(Task.Status.IN_PROGRESS);
            task.setCategory(taskDto.getCategory());

            //autoclaim task if has only one user assigned
            if(CollectionUtils.isEmpty(taskDto.getGroups()) && !CollectionUtils.isEmpty(taskDto.getUsers()) && taskDto.getUsers().size() == 1){
                //get user
                User user = userService.getByEmail(principal.getName());
                task.setClaimedBy(user.getId());
            }

            task = taskRepository.save(task);

            this.addTaskAssignees(task.getId(), taskDto.getUsers(), taskDto.getGroups());

        } else {// edit task by manager
            Optional<Task> optionalTask = taskRepository.findByTaskIdEagerlyActive(taskDto.getId());
            if (!optionalTask.isPresent()) {
                throw new NotFoundException("A task with id:" + taskDto.getId() + " does not exist!");
            }

            Task task = optionalTask.get();
            String managerEmail = principal.getName();

            //check it the principal is manager in order to edit the task
            if (Boolean.FALSE.equals(checkForManager(task, managerEmail))) {
                throw new IllegalAccessException("You can not edit this task because you are not a manager");
            }

            task.setCategory(taskDto.getCategory());
            task.setContent(taskDto.getContent());
            task.setTitle(taskDto.getTitle());

            taskRepository.save(task);
        }
    }

    public Set<AssigneeMember> getActiveAssignees(Task task) {
        Assignee taskAssignee = task.getActiveAssignedUsers().iterator().next();
        return taskAssignee.getActiveAssigneeMemberSet();
    }

    private boolean checkForManager(Task task, String principalEmail) {
        Set<AssigneeMember> assigneeMembers = getActiveAssignees(task);
        Set<Long> groupIds = assigneeMembers.stream().map(AssigneeMember::getGroupId).collect(toSet());
        if (!CollectionUtils.isEmpty(groupIds)) {
            return userGroupService.checkForManager(groupIds, principalEmail);
        }

        return false;
    }

    @Transactional
    public void addTaskAssignees(Long taskId, Set<MemberDto> users, Set<MemberDto> groups) {
        //create assignee
        Assignee assignee = new Assignee();
        assignee.setTaskId(taskId);
        assignee.setActive(true);

        assignee = assigneeRepository.save(assignee);

        //create assignee members
        Set<AssigneeMember> assigneeMembers = new HashSet<>();
        if (!CollectionUtils.isEmpty(users)) {
            for (MemberDto userMember : users) {
                assigneeMembers.add(AssigneeMember.builder()
                        .active(true)
                        .userId(userMember.getId())
                        .assigneeId(assignee.getId())
                        .build());
            }
        }

        if (!CollectionUtils.isEmpty(groups)) {
            for (MemberDto groupMember : groups) {
                assigneeMembers.add(AssigneeMember.builder()
                        .active(true)
                        .groupId(groupMember.getId())
                        .assigneeId(assignee.getId())
                        .build());
            }
        }

        assigneeMemberRepository.saveAll(assigneeMembers);
    }

}
