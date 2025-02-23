import re
from selenium.webdriver.common.by import By  # type: ignore

def scrape_iit_kanpur(college, department, url, driver):
    driver.get(url)
    extracted_data = []
    if department == "project_positions":
        # Find the table rows inside the tbody
        rows = driver.find_elements(By.CSS_SELECTOR, "table tbody tr")
        
        for row in rows[1:]:
            # Get the HTML content of the row
            row_html = row.get_attribute("outerHTML")

            # Define patterns to extract required details
            patterns = {
                "name_of_post": r'<td[^>]*>\s*<p[^>]*>([^<]+)</p>\s*</td>',
                "department": r'<td[^>]*>\s*<p[^>]*>([^<]+)</p>\s*</td>',
                "pi_name": r'<td[^>]*>\s*<p[^>]*>([^<]+)</p>\s*</td>',
                "posting_date": r'<td[^>]*>\s*<p[^>]*>([^<]+)</p>\s*</td>',
                "last_date": r'<td[^>]*>\s*<p[^>]*>([^<]+)</p>\s*</td>',
                "advertisement_link": r'<td[^>]*>\s*<p[^>]*>\s*<a href="([^"]+)"'
            }

            # Extract information using regex
            extracted_row = {}
            matches = re.findall(
                r'<td[^>]*>\s*<p[^>]*>(.*?)</p>\s*</td>', row_html, re.DOTALL
            )

            if matches and len(matches) >= 5:
                extracted_row["name_of_post"] = matches[0].strip()
                extracted_row["department"] = matches[1].strip()
                extracted_row["pi_name"] = matches[2].strip()
                extracted_row["posting_date"] = matches[3].strip()
                extracted_row["last_date"] = matches[4].strip()

            # Extract advertisement link separately
            ad_match = re.search(patterns["advertisement_link"], row_html)
            extracted_row["advertisement_link"] =  "https://iitk.ac.in" + ad_match.group(1).strip() if ad_match else "N/A"

            # Add college & department metadata
            extracted_row["college"] = "IIT Kanpur"
            extracted_row["department"] = department

            extracted_data.append(extracted_row)

    return extracted_data
    

