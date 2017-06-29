# SNP-positions-grouper
This program gets a folder of bed files, a list of positions that are interested in by the user, a "map" file in plink's format, and the desired resolution size.
Its output is a folder of txt files. Each one of txt files is named as a position, like: chr10_4702500
And each position file includes a number of SNP ids, that are within the half range of the given resolution by the user.
A 5k resolution would mean a range-radius of 2500.

 args[0] the list file of close positions, in each line first and third columns are chr numbers, 2nd 4th tokens are the ids of the SNP's. 
So format is like this:
chr1 rs1234 chr1 rs5678

args[1] is resolution size for the files you want to create, is an integer.

args[2] is the map file of SNP's in plink format, an example line is like this:
"1	rs10399749	0	45162"
first column indicates the chromosome number and the second one indicates the id.
3rd and 4th values are not required, so it's okay to have a file with only two columns.

args[3] is the folder of BED files from the dbSNP.
BED files are like a dictionaries for SNP positions, every SNP's position can be found in these files.
Every bed file name should be formatted as such:
"bed_chr_<chr>.bed" <chr> part is either a number or X,Y and so on.
all of these 24 bed files should exist in the folder
