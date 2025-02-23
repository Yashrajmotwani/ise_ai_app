import re
from selenium.webdriver.common.by import By  # type: ignore

def scrape_iit_gandhinagar(college, department, url, driver):
    
    # Open the target URL
    driver.get(url)
    extracted_data = []
    
    if (department == "project_positions"):
        # Find the table rows inside the tbody
        rows = driver.find_elements(By.CSS_SELECTOR, "table tbody tr")

    # Loop through each row and extract data
        for row in rows:
            # Get the HTML content of the row
            row_html = row.get_attribute("outerHTML")

            
            # Extract the individual columns from the row
            if "Post" in row_html and "Discipline" in row_html:  # Skip the header row
                continue

        # Extract the individual columns from the row
            td_tags = row.find_elements(By.TAG_NAME, 'td')

            if len(td_tags) == 7:  # Expected number of columns (7)
                # Extract and clean the data from each column
                extracted_row = {
                    "name_of_post": td_tags[0].text.strip(),
                    "discipline": td_tags[1].text.strip(),
                    "pi_name": td_tags[2].text.strip(),
                    "posting_date": td_tags[3].text.strip(),
                    "last_date": td_tags[4].text.strip(),
                    "advertisement_link": td_tags[5].find_element(By.TAG_NAME, 'a').get_attribute('href') if len(td_tags[5].find_elements(By.TAG_NAME, 'a')) > 0 else "N/A",  # Extract PDF link
                    "status": td_tags[6].find_element(By.TAG_NAME, 'b').text.strip()  # Extract status inside <b> tag
                }
            extracted_row["college"] = "IIT Gandhi Nagar"
            extracted_row["department"] = department
            extracted_data.append(extracted_row)
    else:
        
    
    
        # Find all elements with the "team-info" class
        faculty_cards = driver.find_elements(By.CSS_SELECTOR, ".col-md-4.masonry__item")

        for card in faculty_cards:
            # Extract individual details using tags or class names
            try:
                profile_link = card.find_element(By.CSS_SELECTOR, "a").get_attribute("href")
            except:
                profile_link = "N/A"

            try:
                image_src = card.find_element(By.CSS_SELECTOR, "img").get_attribute("src")
            except:
                image_src = "N/A"

            try:
                name = card.find_element(By.CSS_SELECTOR, "a.h5").text.strip()
            except:
                name = "N/A"

            try:
                position = card.find_element(By.CSS_SELECTOR, "span > strong > b").text.strip()
            except:
                position = "N/A"

            try:
                qualification = card.find_element(By.XPATH, ".//span[contains(text(), 'PhD')]").text.strip()
            except:
                qualification = "N/A"

            try:
                # Handle research areas: this covers cases with `<br>` and plain text
                research_area_element = card.find_element(By.CLASS_NAME, "card__body")
                research_areas = research_area_element.text.split("\n")[-1].strip()
            except:
                research_areas = "N/A"

            # Compile the extracted data into a dictionary
            extracted_row = {
                "college": college,
                "department": department,
                "profile_link": profile_link,
                "image_link": image_src,
                "name": name,
                "position": position,
                "qualification": qualification,
                "areas_of_interest": research_areas,
            }

            
            # Add college and department metadata
            extracted_row["college"] = "IIT GandhiNagar"
            extracted_row["department"] = department

            # Append to the extracted data
            extracted_data.append(extracted_row)
            print("Newly added : ", extracted_row, "\n\n\n")
            
    return extracted_data