import re
from ScrapeIITs import scrape_iit_hyderabad
from selenium.webdriver.common.by import By # type: ignore
from ScrapeIITs.scrape_iit_tirupati import scrape_iit_tirupati
from ScrapeIITs.scrape_iit_gandhinagar import scrape_iit_gandhinagar
from ScrapeIITs.scrape_iit_kanpur import scrape_iit_kanpur
from ScrapeIITs.scrape_iit_hyderabad import scrape_iit_hyderabad
from ScrapeIITs.scrape_iit_bombay import scrape_iit_bombay


function_registry = {
    "iittp": scrape_iit_tirupati,
    "iitg": scrape_iit_gandhinagar,
    "iitk": scrape_iit_kanpur,
    "iith": scrape_iit_hyderabad,
    "iitb": scrape_iit_bombay,
}


    
# Function to call the appropriate scraping function dynamically 
def call_function_based_on_college(college, department, url, driver):
    if college in function_registry:
        try:
            print("college : ", college)
            data = function_registry[college](college, department, url, driver)
            return data
        except Exception as e:
            return {"error": str(e)}
        finally:
            driver.quit()
    else:
        return (f"No function found for input: {college}")