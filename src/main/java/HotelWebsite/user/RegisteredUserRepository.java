package HotelWebsite.user;

import HotelWebsite.user.RegisteredUser.UserIdentifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

interface RegisteredUserRepository extends CrudRepository<RegisteredUser, UserIdentifier> {
    
    @Override
    Streamable<RegisteredUser> findAll();
}