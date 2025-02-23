import re
from selenium.webdriver.common.by import By  # type: ignore

def scrape_iit_tirupati(college, department, url, driver):
    """
    Scrapes project positions and faculty details from IIT Tirupati's website.

    Args:
        college (str): Name of the institution.
        department (str): Department to scrape data for.
        url (str): URL to scrape.
        driver (webdriver): Selenium WebDriver instance.

    Returns:
        list: A list of dictionaries containing extracted project/faculty details.
    """

    # Load the webpage
    driver.get(url)
    extracted_data = []

    if department == "project_positions":
        # Extract project positions from the table
        rows = driver.find_elements(By.CSS_SELECTOR, "table tbody tr")

        for row in rows:
            row_html = row.get_attribute("outerHTML")

            # Define regex patterns for data extraction
            patterns = {
                "posting_date": r'<td>\s*([\d.]+)\s*</td>',
                "name_of_post": r'<td>\s*([^<]+Position[^<]+)\s*</td>',
                "advertisement_link": r'<td><a href="([^"]+)" target="_blank">Advertisement',
                "application_link": r'<a class="text-maroon [^"]*" href="([^"]+)" target="_blank">Click here to apply</a>',
                "last_date": r'Last date for submission application\s*:\s*([\d.]+)',
                "status": r'<td>\s*<div>.*?<a class="text-maroon [^"]*" href="([^"]*)"'
            }

            # Extract and clean data
            extracted_row = {key: (re.search(pattern, row_html, re.DOTALL).group(1).strip() if re.search(pattern, row_html) else "N/A") for key, pattern in patterns.items()}

            # Add metadata
            extracted_row.update({
                "college": "IIT Tirupati",
                "department": department
            })

            extracted_data.append(extracted_row)

    else:
        # Extract faculty details
        faculty_cards = driver.find_elements(By.CLASS_NAME, "single-team")

        for card in faculty_cards:
            card_html = card.get_attribute("outerHTML")

            patterns = {
                "name": r'<h4(?: class="[^"]*")?><a [^>]+>([^<]+)</a></h4>',
                "position": r'<p>\s*<i class="fal fa-graduation-cap [^>]+"></i>\s*([^<]+)</p>',
                "qualification": r'<p class="text-dark">([^<]+)</p>',
                "areas_of_interest": r'(?:<b>Areas of Interest:?</b>:?|<span class="fw-bold text-dark">\s*Areas of Interest:?\s*</span>)\s*([^<]+)',
                "phone": r'<i class="fal fa-phone-alt [^>]+"></i>\s*([\d\s]+)',
                "email": r'(?:Email : )?([^<\s]+@[a-zA-Z0-9.-]+)',
                "image_link": r'<div class="team-thumb">.*?<img src="([^"]+)" alt="img">'
            }

            extracted_row = {key: (re.search(pattern, card_html).group(1).strip() if re.search(pattern, card_html) else "N/A") for key, pattern in patterns.items()}

            extracted_row.update({
                "college": "IIT Tirupati",
                "department": department
            })

            extracted_data.append(extracted_row)

    return extracted_data
