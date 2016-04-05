# Bioportal Import Plugin for Protégé

## Setup
The plugin only works for latest Protégé 5.0.

Installation is simply copying the JAR file from the release into your Protégé directory to the `plugins` folder.

## Usage
### Step 1: 
Go the the class hierarchy view component and open the context menu by right clicking on the class in the hierarchy for which you want to add additional data by means of subclasses.
![Step 1](https://raw.githubusercontent.com/LorenzBuehmann/Bioportal-Protege-Plugin/master/images/step1.png)

### Step 2:
Enter a search term in the text field. Optionally, you can restrict the search to BioPortal categories, groups and ontologies. Then click on `search`.
![Step 2](https://raw.githubusercontent.com/LorenzBuehmann/Bioportal-Protege-Plugin/master/images/step2.png)

### Step 3:
The results of the search are shown in the bottom table, each entry containing the label and ID of the entity  (column 1 and 2) , the ontology in which the entity is contained in (column 3), as well as the attribute in which the search term matched. To continue the data import for a particular entity, please click on the icon in the last column, which will open another dialog.
![Step 3](https://raw.githubusercontent.com/LorenzBuehmann/Bioportal-Protege-Plugin/master/images/step3.png)

### Step 4:
The left hand side of the dialog visualizes (a part of )the class hierarchy in which the chosen entity is contained. The panel on the right hand side will show the data of the currently selected entity from the tree left. You can chose from the facts which data you want to import to your ontology – currently mostly in forms of annotations. BY default, the properties of the source ontology are reused, but you can also use any other annotation property in your ontology instead by using the `Map To` column. Once you're done, please click on the `Import` button.
![Step 4](https://raw.githubusercontent.com/LorenzBuehmann/Bioportal-Protege-Plugin/master/images/step4.png)

### Step 5:
A dialog showing all axioms that will be added to your ontology is shown, just to confirm that no wrong data is imported.
![Step 5](https://raw.githubusercontent.com/LorenzBuehmann/Bioportal-Protege-Plugin/master/images/step5.png)

### Result:
As a result, you can see that the data is now contained in your ontology.
![Result](https://raw.githubusercontent.com/LorenzBuehmann/Bioportal-Protege-Plugin/master/images/step6.png)
