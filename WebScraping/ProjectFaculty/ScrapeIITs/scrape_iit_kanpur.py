import re
from selenium.webdriver.common.by import By  # type: ignore

def scrape_iit_kanpur(college, department, url, driver):
    """
    Scrapes project position details from IIT Kanpur's website.

    Args:
        college (str): Name of the institution.
        department (str): Department to scrape data for.
        url (str): URL to scrape.
        driver (webdriver): Selenium WebDriver instance.

    Returns:
        list: A list of dictionaries containing extracted project position data.
    """

    # Load the webpage
    driver.get(url)
    extracted_data = []

    if department == "project_positions":
        # Locate table rows within the tbody
        rows = driver.find_elements(By.CSS_SELECTOR, "table tbody tr")

        for row in rows[1:]:  # Skip header row
            row_html = row.get_attribute("outerHTML")

            # Regex pattern to extract text content from table cells
            cell_pattern = r'<td[^>]*>\s*<p[^>]*>(.*?)</p>\s*</td>'
            matches = re.findall(cell_pattern, row_html, re.DOTALL)

            if matches and len(matches) >= 5:
                extracted_row = {
                    "name_of_post": matches[0].strip(),
                    "department": matches[1].strip(),
                    "pi_name": matches[2].strip(),
                    "posting_date": matches[3].strip(),
                    "last_date": matches[4].strip()
                }
            else:
                continue  # Skip row if expected data is missing

            # Extract advertisement link separately
            ad_match = re.search(r'<td[^>]*>\s*<p[^>]*>\s*<a href="([^"]+)"', row_html)
            extracted_row["advertisement_link"] = (
                "https://iitk.ac.in" + ad_match.group(1).strip() if ad_match else "N/A"
            )

            # Add metadata
            extracted_row.update({
                "college": "IIT Kanpur",
                "department": department
            })

            extracted_data.append(extracted_row)

    return extracted_data
