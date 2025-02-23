import re
from selenium.webdriver.common.by import By  # type: ignore



def scrape_iit_tirupati(college, department, url, driver):
    # Open the target URL
    driver.get(url)
    extracted_data = []
    
    if (department == "project_positions"):
        # Find the table rows inside the tbody
        rows = driver.find_elements(By.CSS_SELECTOR, "table tbody tr")
        
        # extracted_data = []
        for row in rows:
            # Get the HTML content of the row
            row_html = row.get_attribute("outerHTML")

            # Define patterns to extract required details
            patterns = {
                "posting_date": r'<td>\s*([\d.]+)\s*</td>',
                "name_of_post": r'<td>\s*([^<]+Position[^<]+)\s*</td>',
                "advertisement_link": r'<td><a href="([^"]+)" target="_blank">Advertisement',
                "application_link": r'<a class="text-maroon [^"]*" href="([^"]+)" target="_blank">Click here to apply</a>',
                "last_date": r'Last date for submission application\s*:\s*([\d.]+)',
                "status": r'<td>\s*<div>.*?<a class="text-maroon [^"]*" href="([^"]*)"'
            }

            # Extract information using regex
            extracted_row = {}
            for key, pattern in patterns.items():
                match = re.search(pattern, row_html, re.DOTALL)
                if match:
                    extracted_row[key] = match.group(1).strip()
                else:
                    extracted_row[key] = "N/A"

            extracted_row["college"] = "IIT Tirupati"
            extracted_row["department"] = department
            extracted_data.append(extracted_row)
    else:
        
    
    
    # Find all elements with the "team-info" class
        team_info_elements = driver.find_elements(By.CLASS_NAME, "single-team")
        # print("team_info_elements", team_info_elements)
        # extracted_data = []
        for element in team_info_elements:
            # print("HTML of element:",  element.get_attribute("outerHTML"), "\n\n\n")
            element = element.get_attribute("outerHTML")

            patterns = {
                "name": r'<h4(?: class="[^"]*")?><a [^>]+>([^<]+)</a></h4>',
                "position": r'<p>\s*<i class="fal fa-graduation-cap [^>]+"></i>\s*([^<]+)</p>',
                "qualification": r'<p class="text-dark">([^<]+)</p>',
                "areas_of_interest": r'(?:<b>Areas of Interest:?</b>:?|<span class="fw-bold text-dark">\s*Areas of Interest:?\s*</span>)\s*([^<]+)',
                "phone": r'<i class="fal fa-phone-alt [^>]+"></i>\s*([\d\s]+)',
                "email": r'(?:Email : )?([^<\s]+@[a-zA-Z0-9.-]+)',
                "image_link": r'<div class="team-thumb">.*?<img src="([^"]+)" alt="img">',
            }

            # Extract information using regex
            ext_data = {}

            for key, pattern in patterns.items():
                match = re.search(pattern, element)
                if match:
                    ext_data[key] = match.group(1).strip()
                else:
                        ext_data[key] = "N/A"
            ext_data["college"] = "IIT Tirupati"
            ext_data["department"] = department
            extracted_data.append(ext_data)
        
    # for doc in extracted_data:
    #     if "_id" in doc.keys():
    #         print("changed doc id \n\n")
    #         doc["_id"] = str(doc["_id"])
    return extracted_data