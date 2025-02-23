import re
from selenium.webdriver.common.by import By  # type: ignore

def scrape_iit_hyderabad(college, department, url, driver):
    """
    Scrapes faculty and project position details from IIT Hyderabad's website.

    Args:
        college (str): The name of the college.
        department (str): The department name.
        url (str): The URL to scrape data from.
        driver (webdriver): Selenium WebDriver instance.

    Returns:
        list: Extracted faculty or project position data.
    """

    driver.get(url)
    extracted_data = []

    if department == "project_positions":
        rows = driver.find_elements(By.CSS_SELECTOR, "table tbody tr:not(:first-child)")

        for row in rows:
            row_html = row.get_attribute("outerHTML")

            # Regex patterns for extracting required details
            date_pattern = r"<td[^>]*>(\d{2}-\d{2}-\d{4})</td>"
            link_pattern = r'<a [^>]*href=["\']([^"\']+)["\'][^>]*>(.*?)</a>'

            dates = re.findall(date_pattern, row_html)
            link_match = re.search(link_pattern, row_html)

            if dates and link_match:
                extracted_row = {
                    "posting_date": dates[0],
                    "last_date": dates[1] if len(dates) > 1 else "N/A",
                    "discipline": link_match.group(2).strip(),
                    "advertisement_link": link_match.group(1).strip(),
                    "college": "IIT Hyderabad",
                    "department": department
                }

                # Ensure the advertisement link is absolute
                if not extracted_row["advertisement_link"].startswith("http"):
                    extracted_row["advertisement_link"] = "https://www.iith.ac.in" + extracted_row["advertisement_link"]

                extracted_data.append(extracted_row)

    else:
        faculty_rows = driver.find_elements(By.CLASS_NAME, "row.no-top-margin")

        for faculty_row in faculty_rows:
            faculty_html = faculty_row.get_attribute("outerHTML")

            # Regex patterns for extracting faculty details
            patterns = {
                "name": r"<a [^>]+>\s*<h3>([^<]+)</h3>\s*</a>",
                "position": r"<h4>([^<]+)</h4>",
                "office": r"<h5>(CS-\d+)</h5>",
                "email": r'<a href="mailto:([^"]+)">',
                "areas_of_interest": r"<h5>([^<>]*?(?:,\s*[^<>]+)*)</h5>(?!.*mailto)",
                "profile_link": r'<a class="faculty-link" href="([^"]+)"',
                "image_link": r'<img class="faculty-img-v2" src="([^"]+)"'
            }

            extracted_row = {key: "N/A" for key in patterns}  # Initialize with default values

            # Extract information using regex
            for key, pattern in patterns.items():
                match = re.search(pattern, faculty_html, re.DOTALL)
                if match:
                    extracted_row[key] = match.group(1).strip()

            # Ensure "areas_of_interest" does not contain an office number
            if extracted_row["areas_of_interest"].startswith("CS-"):
                extracted_row["areas_of_interest"] = "N/A"

            # Convert relative image URLs to absolute
            if extracted_row["image_link"].startswith("../"):
                extracted_row["image_link"] = "https://www.iith.ac.in" + extracted_row["image_link"][2:]

            # Add metadata
            extracted_row.update({
                "college": "IIT Hyderabad",
                "department": department
            })

            extracted_data.append(extracted_row)

    return extracted_data
