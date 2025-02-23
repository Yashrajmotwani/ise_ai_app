import re
from selenium import webdriver  # type: ignore
from selenium.webdriver.common.by import By  # type: ignore
from selenium.webdriver.chrome.service import Service  # type: ignore
from selenium.webdriver.chrome.options import Options  # type: ignore
from webdriver_manager.chrome import ChromeDriverManager  # type: ignore

from ScrapeIITs.chooseScrapper import call_function_based_on_college
from ScrapeIITs.links import links  # Dictionary containing URLs for IITs


def scrape_website(college: str, department: str):
    """
    Scrapes data for a specific college and department.

    Args:
        college (str): College name.
        department (str): Department name.

    Returns:
        list | dict: Extracted data from the webpage or an error message.
    """

    # Validate college and department existence in links dictionary
    if college not in links or department not in links[college]:
        return {"error": "Invalid college or department. Format: /<college>/<department>/."}

    # Retrieve target URL
    url = links[college][department]

    # Configure Selenium for headless browsing
    chrome_options = Options()
    chrome_options.add_argument("--headless")  # Run in headless mode (no GUI)
    chrome_options.add_argument("--no-sandbox")  # Bypass OS security restrictions
    chrome_options.add_argument("--disable-dev-shm-usage")  # Prevent shared memory issues

    # Set Chrome binary location (for Linux-based deployments)
    chrome_options.binary_location = "/usr/bin/google-chrome"

    # Initialize WebDriver
    service = Service("/usr/bin/chromedriver-linux64/chromedriver")  
    driver = webdriver.Chrome(service=service, options=chrome_options)

    try:
        # Call the appropriate scraper function based on college
        return call_function_based_on_college(college, department, url, driver)
    finally:
        driver.quit()  # Ensure driver quits after execution


def scrape_website_allInfo():
    """
    Scrapes data for all available colleges and departments.

    Returns:
        list: Aggregated data from all available sources.
    """
    scraped_data = []

    for college, departments in links.items():
        for department in departments:
            data = scrape_website(college, department)
            if "error" not in data:  # Skip errors
                scraped_data.append(data)

    return scraped_data  # Return the aggregated dataset
