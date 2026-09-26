package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.Membership;
import com.corteBrabo.barbershopApi.database.model.MembershipStatus;
import com.corteBrabo.barbershopApi.database.model.Schedule;
import com.corteBrabo.barbershopApi.database.model.ScheduleStatus;
import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.model.UserRole;
import com.corteBrabo.barbershopApi.database.repository.MembershipRepository;
import com.corteBrabo.barbershopApi.database.repository.ScheduleRepository;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.dto.ClientCreateDTO;
import com.corteBrabo.barbershopApi.dto.ClientDetailDTO;
import com.corteBrabo.barbershopApi.dto.ClientResponseDTO;
import com.corteBrabo.barbershopApi.exception.NotFoundException;
import com.corteBrabo.barbershopApi.mapper.ScheduleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final MembershipRepository membershipRepository;
    private final BusinessService businessService;
    private final ScheduleMapper scheduleMapper;

    public ClientService(UserRepository userRepository,
                         ScheduleRepository scheduleRepository,
                         MembershipRepository membershipRepository,
                         BusinessService businessService,
                         ScheduleMapper scheduleMapper) {
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.membershipRepository = membershipRepository;
        this.businessService = businessService;
        this.scheduleMapper = scheduleMapper;
    }

    @Transactional(readOnly = true)
    public List<ClientResponseDTO> search(User currentUser, String search) {
        Long businessId = currentUser.getBusinessId();
        String term = search == null || search.isBlank() ? null : search.trim();
        List<User> clients = userRepository.searchClients(businessId, term);
        if (clients.isEmpty()) return List.of();

        List<Long> ids = clients.stream().map(User::getId).toList();
        Map<Long, List<Schedule>> schedules = scheduleRepository.findByBusiness_IdAndClient_IdIn(businessId, ids).stream()
                .collect(Collectors.groupingBy(s -> s.getClient().getId()));
        Map<Long, Membership> memberships = membershipRepository
                .findByBusiness_IdAndClient_IdInAndStatusNot(businessId, ids, MembershipStatus.CANCELED).stream()
                .collect(Collectors.toMap(m -> m.getClient().getId(), Function.identity(), (a, b) -> a));

        return clients.stream()
                .map(c -> toResponse(c, schedules.getOrDefault(c.getId(), List.of()), memberships.get(c.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public ClientDetailDTO detail(User currentUser, Long id) {
        User client = load(currentUser, id);
        List<Schedule> history = scheduleRepository.findByBusiness_IdAndClient_IdOrderByStartAtDesc(currentUser.getBusinessId(), id);
        Membership membership = membershipRepository.findFirstByClient_IdAndStatusNot(id, MembershipStatus.CANCELED).orElse(null);
        return new ClientDetailDTO(
                toResponse(client, history, membership),
                history.stream().map(scheduleMapper::toResponseDTO).toList()
        );
    }

    @Transactional
    public ClientResponseDTO create(User currentUser, ClientCreateDTO dto) {
        Long businessId = currentUser.getBusinessId();
        if (userRepository.existsByBusiness_IdAndTelefone(businessId, dto.telefone())) {
            throw new IllegalStateException("Já existe um cliente com esse telefone");
        }
        User client = new User();
        client.setBusiness(businessService.load(businessId));
        client.setRole(UserRole.CLIENT);
        apply(client, dto);
        return toResponse(userRepository.save(client), List.of(), null);
    }

    @Transactional
    public ClientResponseDTO update(User currentUser, Long id, ClientCreateDTO dto) {
        User client = load(currentUser, id);
        if (!client.getTelefone().equals(dto.telefone())
                && userRepository.existsByBusiness_IdAndTelefone(currentUser.getBusinessId(), dto.telefone())) {
            throw new IllegalStateException("Já existe um cliente com esse telefone");
        }
        apply(client, dto);
        return detail(currentUser, userRepository.save(client).getId()).client();
    }

    @Transactional
    public void delete(User currentUser, Long id) {
        User client = load(currentUser, id);
        if (scheduleRepository.existsByClient_Id(id)) {
            throw new IllegalStateException("Esse cliente tem histórico de atendimentos e não pode ser excluído");
        }
        userRepository.delete(client);
    }

    private ClientResponseDTO toResponse(User client, List<Schedule> schedules, Membership membership) {
        LocalDateTime now = LocalDateTime.now();
        List<Schedule> done = schedules.stream().filter(s -> s.getStatus() == ScheduleStatus.CONCLUIDO).toList();
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getTelefone(),
                client.getNotes(),
                client.getDate(),
                done.size(),
                schedules.stream().filter(s -> s.getStatus() == ScheduleStatus.FALTOU).count(),
                done.stream().map(Schedule::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add),
                done.stream().map(Schedule::getStartAt).max(Comparator.naturalOrder()).orElse(null),
                schedules.stream()
                        .filter(s -> s.getStartAt().isAfter(now)
                                && (s.getStatus() == ScheduleStatus.CONFIRMADO || s.getStatus() == ScheduleStatus.PENDENTE))
                        .map(Schedule::getStartAt).min(Comparator.naturalOrder()).orElse(null),
                membership == null ? null : membership.getPlan().getName(),
                membership == null ? null : membership.getEffectiveStatus()
        );
    }

    private User load(User currentUser, Long id) {
        return userRepository.findByIdAndBusiness_Id(id, currentUser.getBusinessId())
                .filter(u -> u.getRole() == UserRole.CLIENT)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));
    }

    private static void apply(User client, ClientCreateDTO dto) {
        client.setName(dto.name().trim());
        client.setTelefone(dto.telefone());
        client.setNotes(dto.notes() == null || dto.notes().isBlank() ? null : dto.notes().trim());
    }
}
