# Bioportal Import Plugin for Protégé

## Setup
The plugin only works for latest Protégé 5.0.

Installation is simply copying the JAR file from the release into your Protégé directory to the `plugins` folder.

## Usage
Step 1: 
Go the the class hierarchy view component and open the context menu by right clicking on the class in the hierarchy for which you want to add additional data by means of subclasses.
![Step 1](https://raw.githubusercontent.com/LorenzBuehmann/Bioportal-Protege-Plugin/master/images/step1.png)

Step 2:
Enter a search term in the text field. Optionally, you can restrict the search to BioPortal categories, groups and ontologies. Then click on `search`.
![Step 2](https://raw.githubusercontent.com/LorenzBuehmann/Bioportal-Protege-Plugin/master/images/step2.png)

Step 3:
The results of the search are shown in the bottom table, each entry containing the label and ID of the entity  (column 1 and 2) , the ontology in which the entity is contained in (column 3), as well as the attribute in which the search term matched. To continue the data import for a particular entity, please click on the icon in the last column, which will the nopen another dialog.
![Step 3](https://raw.githubusercontent.com/LorenzBuehmann/Bioportal-Protege-Plugin/master/images/step3.png)

Step 4:
![Step 4](https://raw.githubusercontent.com/LorenzBuehmann/Bioportal-Protege-Plugin/master/images/step4.png)

Step 5:
![Step 5](https://raw.githubusercontent.com/LorenzBuehmann/Bioportal-Protege-Plugin/master/images/step5.png)

Result:
![Result](https://raw.githubusercontent.com/LorenzBuehmann/Bioportal-Protege-Plugin/master/images/step6.png)
