//inputDir1 = File.openDialog("INPUT Folder");
//inputDir2 = File.openDialog("OUTPUT Folder");

inputDir1 = "/home/.../DCM/_IN/";
inputDir2 = "/home/.../DCM/_OUT/";

fileList1 = getFileList(inputDir1);

for (i = 0; i < fileList1.length; i++) {
	  file1 = fileList1[i];
	  
	if (i == 0) {
		fileX = fileList1[i + 1];
		inputDirX = inputDir1; 
		outputDir = inputDir2;
		i = i + 1;
		}
	else { 
		fileList2 = getFileList(inputDir2);
		fileX = fileList2[0];
		inputDirX = inputDir2;
		outputDir = inputDir2;
		}
			
	  open(inputDir1+"/"+file1);
	  id1 = getImageID();
	  open(inputDirX+"/"+fileX);
	  id2 = getImageID();
	  
	  imageCalculator("Add create", id1, id2);

	  saveAs("Tiff", outputDir + "/File_Final");
 	  run("Close All"); 
}

open(inputDir2+"/File_Final.tif");
saveAs("Text Image", inputDir2+"/File_Final.txt");

run("Duplicate...", "title=File_Final-1.tif");

run("Find Maxima...", "noise=10 output=List");
run("Find Maxima...", "noise=10 output=[Point Selection]");

run("Grid ", "grid=Lines area=100 color=Cyan");


