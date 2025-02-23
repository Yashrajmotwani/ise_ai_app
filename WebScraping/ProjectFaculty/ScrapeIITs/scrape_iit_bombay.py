import re
from selenium.webdriver.common.by import By  # type: ignore

def scrape_iit_bombay(college: str, department: str, url: str, driver) -> list:
    """
    Scrapes project positions from the IIT Bombay careers page.

    Parameters:
    - college (str): Name of the college (IIT Bombay).
    - department (str): Department to filter positions.
    - url (str): Webpage URL to scrape.
    - driver: Selenium WebDriver instance.

    Returns:
    - List[dict]: Extracted project positions with relevant details.
    """
    driver.get(url)
    extracted_data = []

    if department == "project_positions":
        job_sections = driver.find_elements(By.CSS_SELECTOR, ".career-wrap.accordion-section.jobtitle")

        for job in job_sections:
            extracted_row = {}

            # Extract project title
            title_element = job.find_element(By.CSS_SELECTOR, ".accordion-section-title")
            extracted_row["project_title"] = title_element.text.strip()

            # Get job details as HTML
            content_html = job.find_element(By.CSS_SELECTOR, ".accordion-section-content").get_attribute("outerHTML")

            # Define regex patterns for extracting details
            patterns = {
                "name_of_post": r"<strong>Position Title:.*?</strong>\s*(.*?)</p>",
                "duration": r"<strong>Duration:.*?</strong>\s*(.*?)</p>",
                "salary_band": r"<strong>Salary Band:.*?</strong>\s*(.*?)</p>",
                "location": r"<strong>Location:.*?</strong>\s*(.*?)</p>",
                "advertisement_link": r'<a class="button" href="([^"]+)"'
            }

            # Apply regex patterns to extract information
            for key, pattern in patterns.items():
                match = re.search(pattern, content_html, re.DOTALL)
                extracted_row[key] = match.group(1).strip() if match else "N/A"

            # Add metadata
            extracted_row.update({"college": college, "department": department})
            extracted_data.append(extracted_row)

    return extracted_data
