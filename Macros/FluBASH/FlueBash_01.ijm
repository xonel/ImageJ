inputDir1 = "/home/.../DCM/0/";
inputDir2 = "/home/.../DCM/00/";
outputDir = "/home/.../DCM/000/";

fileList1 = getFileList(inputDir1);

for (i = 0; i < fileList1.length; i = i + 2) {
  file1 = fileList1[i];
  file2 = fileList1[i + 1];

  open(inputDir1+"/"+file1);
  id1 = getImageID();

  open(inputDir1+"/"+file2);
  id2 = getImageID();

  imageCalculator("Add create", id1, id2);
  id3 = getImageID();

  outName = File.getName(file1) + "-" + File.getName(file2);
  saveAs("Tiff", inputDir2 + "/" + outName);
  }

if (i >= fileList1.length) {

	fileList2 = getFileList(inputDir2);

	for (i = 0; i < fileList2.length; i = i + 2) {
	  file1 = fileList2[i];
	  file2 = fileList2[i + 1];

	  open(inputDir2+"/"+file1);
	  id1 = getImageID();

	  open(inputDir2+"/"+file2);
	  id2 = getImageID();

	  imageCalculator("Add create", id1, id2);
	  id3 = getImageID();

	  outName = File.getName(file1) + "-" + File.getName(file2);
	  saveAs("Tiff", outputDir + "/" + outName);
	}

} 
