package soat.project.fastfoodsoat.application.usecase.staff.auth;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import soat.project.fastfoodsoat.application.command.staff.auth.AuthStaffCommand;
import soat.project.fastfoodsoat.application.gateway.StaffRepositoryGateway;
import soat.project.fastfoodsoat.application.gateway.TokenService;
import soat.project.fastfoodsoat.application.usecase.UseCaseTest;
import soat.project.fastfoodsoat.domain.exception.DomainException;
import soat.project.fastfoodsoat.domain.staff.Staff;
import soat.project.fastfoodsoat.domain.token.Token;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthStaffUseCaseTest extends UseCaseTest {

    @InjectMocks
    private AuthStaffUseCaseImpl useCase;

    @Mock
    private StaffRepositoryGateway staffRepositoryGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(staffRepositoryGateway);
    }

    @Test
    void givenAValidCommandWithEmail_whenCallsAuthStaff_shouldReturnToken() {
        // Given
        final var staff = Staff.newStaff("john", "john@email.com", "12345678901", List.of());
        final var token = new Token("token", "Bearer", 43200L, List.of());

        final var command = new AuthStaffCommand(staff.getEmail());

        when(staffRepositoryGateway.findByEmail(anyString())).thenReturn(Optional.of(staff));

        // When
        final var actualOutput = useCase.execute(command);

        // Then
        assertNotNull(actualOutput);
        assertNotNull(actualOutput.name());
        assertNotNull(actualOutput.email());
        assertNotNull(actualOutput.cpf());
        assertNotNull(actualOutput.roles());

        verify(staffRepositoryGateway, times(1)).findByEmail(anyString());
        Mockito.verifyNoMoreInteractions(staffRepositoryGateway);
    }

    @Test
    void givenAValidCommandWithCpf_whenCallsAuthStaff_shouldReturnToken() {
        // Given
        final var staff = Staff.newStaff("john", "john@email.com", "12345678901", List.of());
        final var token = new Token("token", "Bearer", 43200L, List.of());

        final var command = new AuthStaffCommand(staff.getCpf().getValue());

        when(staffRepositoryGateway.findByEmail(anyString())).thenReturn(Optional.empty());
        when(staffRepositoryGateway.findByCpf(any())).thenReturn(Optional.of(staff));

        // When
        final var actualOutput = useCase.execute(command);

        // Then
        assertNotNull(actualOutput);
        assertNotNull(actualOutput.name());
        assertNotNull(actualOutput.email());
        assertNotNull(actualOutput.cpf());
        assertNotNull(actualOutput.roles());

        verify(staffRepositoryGateway, times(1)).findByEmail(anyString());
        verify(staffRepositoryGateway, times(1)).findByCpf(any());
    }

    @Test
    void givenACommandoWithInvalidStaff_whenCallsAuthStaff_shouldReceiveError() {
        // Given
        final var staff = Staff.newStaff("john", "john@email.com", "12345678901", List.of());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "staff not found";

        final var command = new AuthStaffCommand(staff.getCpf().getValue());

        when(staffRepositoryGateway.findByEmail(anyString())).thenReturn(Optional.empty());
        when(staffRepositoryGateway.findByCpf(any())).thenReturn(Optional.empty());

        // When
        final var actualException = assertThrows(
                DomainException.class,
                () -> useCase.execute(command)
        );

        // Then
        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().getFirst().message());

        verify(staffRepositoryGateway, times(1)).findByEmail(anyString());
        verify(staffRepositoryGateway, times(1)).findByCpf(any());
        Mockito.verifyNoMoreInteractions(staffRepositoryGateway);
    }

    @Test
    void givenACommandoWithInvalidIdentification_whenCallsAuthStaff_shouldReceiveError() {
        // Given
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "invalid identification";

        final var command = new AuthStaffCommand(null);

        // When
        final var actualException = assertThrows(
                DomainException.class,
                () -> useCase.execute(command)
        );

        // Then
        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().getFirst().message());

        Mockito.verifyNoMoreInteractions(staffRepositoryGateway);
    }

}