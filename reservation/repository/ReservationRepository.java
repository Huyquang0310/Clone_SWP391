package fu.libmanagement.reservation.repository;

import fu.libmanagement.common.enums.ReservationStatus;
import fu.libmanagement.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @EntityGraph(attributePaths = "book")
    Page<Reservation> findByReaderId(Long readerId, Pageable pageable);

    Optional<Reservation> findByIdAndReaderId(Long id, Long readerId);

    boolean existsByReaderIdAndBookIdAndStatusIn(Long readerId, Long bookId, Collection<ReservationStatus> statuses);

    List<Reservation> findByReaderIdAndBookIdAndStatusIn(Long readerId, Long bookId, Collection<ReservationStatus> statuses);
}
