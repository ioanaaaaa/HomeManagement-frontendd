package com.fmi.relovut.controllers;

import com.fmi.relovut.dto.GroupDto;
import com.fmi.relovut.models.Group;
import com.fmi.relovut.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/add-edit")
    public ResponseEntity createOrUpdateGroup(@RequestBody GroupDto groupDto){
        groupService.createOrUpdateGroup(groupDto);

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("")
    public Set<GroupDto> getGroups(){
        return GroupDto.toDtos(groupService.getGroups());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteGroup(@PathVariable("id") Long id){
        groupService.deleteGroupById(id);

        return new ResponseEntity(HttpStatus.OK);
    }
}
