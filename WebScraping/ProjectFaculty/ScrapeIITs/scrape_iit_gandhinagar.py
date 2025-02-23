import re
from selenium.webdriver.common.by import By  # type: ignore

def scrape_iit_gandhinagar(college, department, url, driver):
    """
    Scrapes faculty and project position data from IIT Gandhinagar's website.
    """
    driver.get(url)
    extracted_data = []
    
    if department == "project_positions":
        rows = driver.find_elements(By.CSS_SELECTOR, "table tbody tr")

        for row in rows:
            row_html = row.get_attribute("outerHTML")
            
            # Skip header row
            if "Post" in row_html and "Discipline" in row_html:
                continue
            
            td_tags = row.find_elements(By.TAG_NAME, 'td')
            
            if len(td_tags) == 7:
                extracted_row = {
                    "name_of_post": td_tags[0].text.strip(),
                    "discipline": td_tags[1].text.strip(),
                    "pi_name": td_tags[2].text.strip(),
                    "posting_date": td_tags[3].text.strip(),
                    "last_date": td_tags[4].text.strip(),
                    "advertisement_link": td_tags[5].find_element(By.TAG_NAME, 'a').get_attribute('href') if td_tags[5].find_elements(By.TAG_NAME, 'a') else "N/A",
                    "status": td_tags[6].find_element(By.TAG_NAME, 'b').text.strip()
                }
                extracted_row["college"] = "IIT Gandhinagar"
                extracted_row["department"] = department
                extracted_data.append(extracted_row)
    else:
        faculty_cards = driver.find_elements(By.CSS_SELECTOR, ".col-md-4.masonry__item")

        for card in faculty_cards:
            extracted_row = {
                "college": "IIT Gandhinagar",
                "department": department,
                "profile_link": card.find_element(By.CSS_SELECTOR, "a").get_attribute("href") if card.find_elements(By.CSS_SELECTOR, "a") else "N/A",
                "image_link": card.find_element(By.CSS_SELECTOR, "img").get_attribute("src") if card.find_elements(By.CSS_SELECTOR, "img") else "N/A",
                "name": card.find_element(By.CSS_SELECTOR, "a.h5").text.strip() if card.find_elements(By.CSS_SELECTOR, "a.h5") else "N/A",
                "position": card.find_element(By.CSS_SELECTOR, "span > strong > b").text.strip() if card.find_elements(By.CSS_SELECTOR, "span > strong > b") else "N/A",
                "qualification": next((span.text.strip() for span in card.find_elements(By.XPATH, ".//span") if "PhD" in span.text), "N/A"),
                "areas_of_interest": card.find_element(By.CLASS_NAME, "card__body").text.split("\n")[-1].strip() if card.find_elements(By.CLASS_NAME, "card__body") else "N/A"
            }
            extracted_data.append(extracted_row)
            print("Newly added:", extracted_row, "\n\n")
    
    return extracted_data
