package com.example.taskManager.repository;

import com.example.taskManager.entity.Group;
import com.example.taskManager.entity.GroupMember;
import com.example.taskManager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

    @Query("SELECT gm.group FROM GroupMember gm WHERE gm.user = :user")
    Page<Group> findAllGroupsByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT gm.group FROM GroupMember gm WHERE gm.user = :user")
    List<Group> findAllGroupsByUser(@Param("user") User user);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId")
    Page<GroupMember> findAllMembersByGroup(@Param("groupId") UUID groupId, Pageable pageable);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId")
    List<GroupMember> findAllMembersByGroup(@Param("groupId") UUID groupId);

    @Query("SELECT COUNT(gm) > 0 FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.user.id = :userId")
    boolean isUserMemberOfGroup(@Param("groupId") UUID groupId, @Param("userId") UUID userId);
}