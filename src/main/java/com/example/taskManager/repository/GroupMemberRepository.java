package com.example.taskManager.repository;

import com.example.taskManager.entity.GroupMember;
import com.example.taskManager.entity.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
}