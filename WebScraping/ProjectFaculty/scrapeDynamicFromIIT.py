import re
from ScrapeIITs.chooseScrapper import call_function_based_on_college
from selenium import webdriver  # type: ignore # Selenium WebDriver for browser automation
from selenium.webdriver.common.by import By   # type: ignore
from selenium.webdriver.chrome.service import Service  # type: ignore
from selenium.webdriver.chrome.options import Options  # type: ignore
from webdriver_manager.chrome import ChromeDriverManager # type: ignore # To automatically manage ChromeDriver
from ScrapeIITs.links import links  # Import a dictionary containing URLs for different colleges and departments

def scrape_website(college, department):    
    """
    Scrapes data from the specified college and department webpage using Selenium.
    
    Args:
        college (str): The name of the college.
        department (str): The department within the college.

    Returns:
        dict or list: Extracted data from the webpage or an error message if invalid input.
    """
    
    # Validate if the given college and department exist in the links dictionary
    if college not in links or department not in links[college]:
        return {"error": "Invalid college or department or URL format. The correct format is /<college>/<department>/."}

    # Get the target URL for scraping
    url = links[college][department]

    # Configure Selenium Chrome options for headless mode (no GUI)
    chrome_options = Options()
    chrome_options.add_argument("--headless")  # Enables headless mode for non-GUI execution
    chrome_options.add_argument("--no-sandbox")  # Bypasses OS security restrictions
    chrome_options.add_argument("--disable-dev-shm-usage")  # Prevents shared memory issues

    # Set the Chrome binary location (for Linux-based deployments)
    chrome_options.binary_location = "/usr/bin/google-chrome"  

    # Specify the path to Chromedriver (Ensure Chromedriver is correctly installed and located)
    service = Service("/usr/bin/chromedriver-linux64/chromedriver")  
    driver = webdriver.Chrome(service=service, options=chrome_options)  # Initialize Selenium WebDriver

    # Call the appropriate scraping function based on the college name
    data = call_function_based_on_college(college, department, url, driver)

    # Return the extracted data
    return data

def scrape_website_allInfo():
    """
    Scrapes data for all colleges and departments present in the `links` dictionary.
    
    Returns:
        list: A list containing extracted data from all available colleges and departments.
    """
    
    scraped_data = []  # List to store data from all scrapes

    # Iterate over each college and its departments
    for college, departments in links.items():
        for department in departments:
            # Scrape data for each department
            data = scrape_website(college, department)
            
            # Skip if an error occurs (e.g., invalid URL)
            if "error" in data:
                continue

            # Append extracted data to the list
            scraped_data.append(data)
    
    return scraped_data  # Return the complete dataset
