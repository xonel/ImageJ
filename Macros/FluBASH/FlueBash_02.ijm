inputDir1 = "/home/.../DCM/_IN/";
inputDir2 = "/home/.../DCM/_OUT/";

fileList1 = getFileList(inputDir1);

for (i = 0; i < fileList1.length; i = i + 2) {
	  file1 = fileList1[i];
	  file11 = fileList1[i + 1];
	  
	if (i == 0) {
		fileX = file11;
		inputDirX = inputDir1; 
		outputDir = inputDir2;
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
}

saveAs("Text Image", inputDir2+"/File_Final.txt");
run("Close All");
open(inputDir2+"/File_Final.tif");
