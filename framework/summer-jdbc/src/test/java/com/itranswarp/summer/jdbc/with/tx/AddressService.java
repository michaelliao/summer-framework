package com.itranswarp.summer.jdbc.with.tx;

import java.util.List;

import com.itranswarp.summer.annotation.Autowired;
import com.itranswarp.summer.annotation.Component;
import com.itranswarp.summer.annotation.Transactional;
import com.itranswarp.summer.jdbc.JdbcTemplate;
import com.itranswarp.summer.jdbc.JdbcTestBase;

@Component
@Transactional
public class AddressService {

    @Autowired
    UserService userService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void addAddress(Address... addresses) {
        for (Address address : addresses) {
            // check if userId is exist:
            userService.getUser(address.userId);
            jdbcTemplate.update(JdbcTestBase.INSERT_ADDRESS, address.userId, address.address, address.zip);
        }
    }

    public List<Address> getAddresses(int userId) {
        return jdbcTemplate.queryForList(JdbcTestBase.SELECT_ADDRESS_BY_USERID, Address.class, userId);
    }

    public void deleteAddress(int userId) {
        jdbcTemplate.update(JdbcTestBase.DELETE_ADDRESS_BY_USERID, userId);
        if (userId == 1) {
            throw new RuntimeException("Rollback delete for user id = 1");
        }
    }
}
