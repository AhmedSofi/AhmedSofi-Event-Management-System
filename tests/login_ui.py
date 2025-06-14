import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
import time

class MyEventsPageTest(unittest.TestCase):
    def setUp(self):
        self.driver = webdriver.Chrome()
        self.driver.get("http://localhost:8080/EventManagementSystemV2/login")

    def test_successful_login(self):
        driver = self.driver

        username = driver.find_elements(By.CSS_SELECTOR, 'input[name="username"]')
        self.assertTrue(username, "Username field not found")
        username[0].send_keys("soliman")

        password = driver.find_elements(By.CSS_SELECTOR, 'input[name="password"]')
        self.assertTrue(password, "Password field not found")
        password[0].send_keys("soliman")
        password[0].send_keys(Keys.RETURN)

        time.sleep(2)

        welcome_div = driver.find_elements(By.CSS_SELECTOR, 'div.welcome')
        self.assertTrue(welcome_div, "Welcome message not found after login")

    def test_failed_login_invalid_credentials(self):
        driver = self.driver

        username = driver.find_elements(By.CSS_SELECTOR, 'input[name="username"]')
        self.assertTrue(username, "Username field not found")
        username[0].send_keys("wronguser")

        password = driver.find_elements(By.CSS_SELECTOR, 'input[name="password"]')
        self.assertTrue(password, "Password field not found")
        password[0].send_keys("wrongpassword")
        password[0].send_keys(Keys.RETURN)

        time.sleep(2)

        # Check if error message is displayed
        error_div = driver.find_elements(By.CSS_SELECTOR, 'div.alert.alert-danger[role="alert"]')
        self.assertTrue(error_div, "Error message not displayed for invalid login")

    def tearDown(self):
        self.driver.quit()

if __name__ == "__main__":
    unittest.main()
