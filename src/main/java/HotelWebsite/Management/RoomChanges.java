package HotelWebsite.Management;


import HotelWebsite.RoomCatalog.CatalogRepository;
import HotelWebsite.RoomCatalog.Room.DedicatedRoom;
import HotelWebsite.RoomCatalog.Room.EquipmentItem;
import org.salespointframework.catalog.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RoomChanges {

	private final CatalogRepository repo;
	public RoomChanges(CatalogRepository repo){
		this.repo= repo;
	}

	public List<EquipmentItem> getAllEquipment(){
		List<DedicatedRoom> rooms = repo.findAll().stream().toList();
		Set<EquipmentItem> itemset = new HashSet<>();
		for (DedicatedRoom room : rooms) {
			itemset.addAll(room.getEquipment());
		}
		List<EquipmentItem> items = new ArrayList<>(itemset.stream().toList());
		Collections.sort(items);
		return items;
	}

	@Transactional
	public void updateItem(Long id, double newPrice){
		List<EquipmentItem> equipment = getAllEquipment();
		for (EquipmentItem item : equipment){
			if (item.getId().equals(id)) {
				item.setValue(newPrice);
			}
		}
	}

	@Transactional
	public void addItem(String name,
						double price,
						Product.ProductIdentifier[] rooms){
		EquipmentItem item = new EquipmentItem(name, price);
		for (Product.ProductIdentifier id : rooms){
			Optional<DedicatedRoom> room = repo.findById(id);
			room.ifPresent(dedicatedRoom -> dedicatedRoom.addEquipment(item));
		}
	}

	@Transactional
	public void addItemToRoom(Product.ProductIdentifier roomid, Long[] items){
		DedicatedRoom room = repo.findById(roomid).get();
		for (EquipmentItem item : getAllEquipment()) {
			for (long id : items) {
				if (item.getId().equals(id)){
					room.addEquipment(item);
				}
			}
		}
	}

	@Transactional
	public void removeItemFromRoom(Product.ProductIdentifier roomid, Long[] items){
		DedicatedRoom room = repo.findById(roomid).get();
		for (EquipmentItem item : getAllEquipment()) {
			for (long id : items) {
				if (item.getId().equals(id)){
					room.removeEquipment(item);
				}
			}
		}
	}

	public List<EquipmentItem> ItemNotInRoom(Product.ProductIdentifier id) {
		DedicatedRoom room = repo.findById(id).get();
		List<EquipmentItem> result = getAllEquipment();
		result.removeAll(room.getEquipment());
		return result;
	}





}
