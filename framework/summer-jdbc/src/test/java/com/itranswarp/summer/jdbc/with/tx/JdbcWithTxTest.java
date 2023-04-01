package com.itranswarp.summer.jdbc.with.tx;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.itranswarp.summer.context.AnnotationConfigApplicationContext;
import com.itranswarp.summer.exception.TransactionException;
import com.itranswarp.summer.jdbc.JdbcTemplate;
import com.itranswarp.summer.jdbc.JdbcTestBase;

public class JdbcWithTxTest extends JdbcTestBase {

    @Test
    public void testJdbcWithTx() {
        try (var ctx = new AnnotationConfigApplicationContext(JdbcWithTxApplication.class, createPropertyResolver())) {
            JdbcTemplate jdbcTemplate = ctx.getBean(JdbcTemplate.class);
            jdbcTemplate.update(CREATE_USER);
            jdbcTemplate.update(CREATE_ADDRESS);

            UserService userService = ctx.getBean(UserService.class);
            AddressService addressService = ctx.getBean(AddressService.class);
            // proxied:
            assertNotSame(UserService.class, userService.getClass());
            assertNotSame(AddressService.class, addressService.getClass());
            // proxy object is not inject:
            assertNull(userService.addressService);
            assertNull(addressService.userService);

            // insert user:
            User bob = userService.createUser("Bob", 12);
            assertEquals(1, bob.id);

            // insert addresses:
            Address addr1 = new Address(bob.id, "Broadway, New York", 10012);
            Address addr2 = new Address(bob.id, "Fifth Avenue, New York", 10080);
            // NOTE user not exist for addr3:
            Address addr3 = new Address(bob.id + 1, "Ocean Drive, Miami, Florida", 33411);
            assertThrows(TransactionException.class, () -> {
                addressService.addAddress(addr1, addr2, addr3);
            });

            // ALL address should not inserted:
            assertTrue(addressService.getAddresses(bob.id).isEmpty());

            // insert addr1, addr2 for Bob only:
            addressService.addAddress(addr1, addr2);
            assertEquals(2, addressService.getAddresses(bob.id).size());

            // now delete bob will cause rollback:
            assertThrows(TransactionException.class, () -> {
                userService.deleteUser(bob);
            });

            // bob and his addresses still exist:
            assertEquals("Bob", userService.getUser(1).name);
            assertEquals(2, addressService.getAddresses(bob.id).size());
        }
        // re-open db and query:
        try (var ctx = new AnnotationConfigApplicationContext(JdbcWithTxApplication.class, createPropertyResolver())) {
            AddressService addressService = ctx.getBean(AddressService.class);
            List<Address> addressesOfBob = addressService.getAddresses(1);
            assertEquals(2, addressesOfBob.size());
        }
    }
}
