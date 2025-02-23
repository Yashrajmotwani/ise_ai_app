import re
from selenium.webdriver.common.by import By  # type: ignore

def scrape_iit_bombay(college, department, url, driver):
    driver.get(url)
    extracted_data = []

    if department == "project_positions":
        job_sections = driver.find_elements(By.CSS_SELECTOR, ".career-wrap.accordion-section.jobtitle")

        for job in job_sections:
            extracted_row = {}

            # Extract project title
            title_element = job.find_element(By.CSS_SELECTOR, ".accordion-section-title")
            extracted_row["project_title"] = title_element.text.strip()

            # Get job details
            content_html = job.find_element(By.CSS_SELECTOR, ".accordion-section-content").get_attribute("outerHTML")

            # Define patterns to extract required details
            patterns = {
                "name_of_post": r"<strong>Position Title:.*?</strong>\s*(.*?)</p>",
                "duration": r"<strong>Duration:.*?</strong>\s*(.*?)</p>",
                "salary_band": r"<strong>Salary Band:.*?</strong>\s*(.*?)</p>",
                "location": r"<strong>Location:.*?</strong>\s*(.*?)</p>",
                "advertisement_link": r'<a class="button" href="([^"]+)"'
            }

            # Extract information using regex
            for key, pattern in patterns.items():
                match = re.search(pattern, content_html, re.DOTALL)
                extracted_row[key] = match.group(1).strip() if match else "N/A"

            # Add metadata
            extracted_row["college"] = "IIT Bombay"
            extracted_row["department"] = department

            extracted_data.append(extracted_row)

    return extracted_data
