package sqt.Selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserManagementTest extends BaseTest {

    private static final String TEST_PASSWORD = "Testpass@123";

    private String generateTestUsername() {
        return "user_" + System.currentTimeMillis();
    }

    @Test
    @Order(1)
    @DisplayName("Add a new Admin user and verify it appears in list")
    void addAdminUserSuccessfully() throws InterruptedException {
        String username = generateTestUsername();

        adminUsersPage.navigateTo();
        adminUsersPage.addUser("Admin", "a", username, TEST_PASSWORD);

        adminUsersPage.searchByUsername(username);
        assertEquals(1, adminUsersPage.getResultCount(),
                "Newly added Admin user should appear exactly once in search results");
    }

    @Test
    @Order(2)
    @DisplayName("Add a new ESS user and verify it appears in list")
    void addEssUserSuccessfully() throws InterruptedException {
        String username = generateTestUsername();

        adminUsersPage.navigateTo();
        adminUsersPage.addUser("ESS", "a", username, TEST_PASSWORD);

        adminUsersPage.searchByUsername(username);
        assertFalse(adminUsersPage.isNoRecordFound(),
                "Newly added ESS user should be found after being added");
    }

    @Test
    @Order(3)
    @DisplayName("Delete an existing user and verify it no longer appears in list")
    void deleteUserSuccessfully() throws InterruptedException {
        String username = generateTestUsername();

        adminUsersPage.navigateTo();
        adminUsersPage.addUser("Admin", "a", username, TEST_PASSWORD);

        adminUsersPage.searchByUsername(username);
        assertFalse(adminUsersPage.isNoRecordFound(), "User must exist before deletion");

        adminUsersPage.deleteFirstResult();

        adminUsersPage.searchByUsername(username);
        assertTrue(adminUsersPage.isNoRecordFound(),
                "Deleted user should not appear in search results");
    }

    @Test
    @Order(4)
    @DisplayName("Search for a non-existent username returns no records")
    void searchNonExistentUserReturnsNoRecords() {
        adminUsersPage.navigateTo();
        adminUsersPage.searchByUsername("nonexistent_xyz_999");
        assertTrue(adminUsersPage.isNoRecordFound(),
                "Search for an unknown username should show 'No Records Found'");
    }
}