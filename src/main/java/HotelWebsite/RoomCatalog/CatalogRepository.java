package HotelWebsite.RoomCatalog;


import HotelWebsite.RoomCatalog.Room.DedicatedRoom;
import org.salespointframework.catalog.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Set;

@Repository
public interface CatalogRepository extends CrudRepository<DedicatedRoom, Product.ProductIdentifier> {
	public Set<DedicatedRoom> findAll();

	public Set<DedicatedRoom> findAllByDedicated(boolean dedicated);
}
