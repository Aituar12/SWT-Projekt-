package HotelWebsite.Management;

import HotelWebsite.Management.TransactionEntry.TransactionIdentifier;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * A repository interface to manage {@link TransactionEntry} instances.
 *
 * @author Celina Stransky
 */
@Repository
public interface StatisticRepository extends CrudRepository<TransactionEntry,TransactionIdentifier> {
}
