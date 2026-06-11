#!/bin/bash                                                                                                                                        
                                                                                                                                                   
if (( $# != 1))                                                                                                                                    
then                                                                                                                                               
        echo "Usage active.sh [version]"                                                                                                           
        exit 1                                                                                                                                     
fi                                                                                                                                                 
                                                                                                                                                   
BASE=ferris-tadd-$1-prod                                                                                                                           
                                                                                                                                                   
if [ ! -s "$BASE.tar.gz" ]; then                                                                                                                   
        echo "File does not exist: $BASE.tar.gz"                                                                                                   
        exit 1                                                                                                                                     
fi                                                                                                                                                 
                                                                                                                                                   
if [ -d "$BASE" ]; then                                                                                                                            
        echo "Deleting directory $BASE"                                                                                                            
                rm -rf $BASE                                                                                                                       
fi                                                                                                                                                 
                                                                                                                                                   
tar xvzf $BASE.tar.gz                                                                                                                              
                                                                                                                                                   
                                                                                                                                                   
#####################                                                                                                                              
## CONF                                                                                                                                            
#####################                                                                                                                              
cd $BASE                                                                                                                                           
cd conf                                                                                                                                            
                                                                                                                                                   
echo "Linking to properties"                                                                                                                            
rm -f tadd.properties                                                                                                                              
ln -s ../../tadd.properties                                                                                                                        
                                                                                                                                                   
####################                                                                                                                               
## ACTIVE                                                                                                                                          
####################                                                                                                                               
cd ..                                                                                                                                              
echo "Remaking active"                                                                                                                             
rm active                                                                                                                                          
ln -s $BASE active
