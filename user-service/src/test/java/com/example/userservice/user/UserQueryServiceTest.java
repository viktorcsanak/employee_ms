package com.example.userservice.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.userservice.user.dto.UserSearchRequest;
import com.example.userservice.user.model.User;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceTest {

  @Mock private UserRepository repo;

  @InjectMocks private UserQueryService service;

  @Test
  void search_nullRequest_returnsAllUsers() {
    when(repo.findAll()).thenReturn(List.of(new User()));

    List<User> result = service.search(null);

    assertEquals(1, result.size());
    verify(repo).findAll();
  }

  @Test
  void search_withRequest_callsSpecification() {
    UserSearchRequest req = mock(UserSearchRequest.class);
    when(req.firstName()).thenReturn("John");
    when(req.email()).thenReturn(null);

    when(repo.findAll(org.mockito.ArgumentMatchers.<Specification<User>>any()))
        .thenReturn(List.of(new User()));

    List<User> result = service.search(req);

    assertEquals(1, result.size());
    verify(repo).findAll(org.mockito.ArgumentMatchers.<Specification<User>>any());
  }
}
