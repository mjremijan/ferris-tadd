#!/bin/bash                                                                                                                      
                                                                                                                                 
LD=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )                                                                             
                                                                                                                                 
echo "LAUNCH DIRECTORY"                                                                                                          
echo "----------------"                                                                                                          
echo "${LD}"                                                                                                                     
echo $'\n'                                                                                                                       
                                                                                                                                 
echo "CURRENT DIRECTORY"                                                                                                         
echo "-----------------"                                                                                                         
pwd                                                                                                                              
echo $'\n'                                                                                                                       
                                                                                                                                 
PD=${LD}/..                                                                                                                      
JAVA_BIN=${PD}/../jre/bin/java                                                                                                   
echo "JAVA BIN"                                                                                                                  
echo "--------"                                                                                                                  
echo "${JAVA_BIN}"                                                                                                               
echo $'\n'                                                                                                                       
                                                                                                                                 
echo "JAVA VERSION"                                                                                                              
echo "------------"                                                                                                              
${JAVA_BIN} -version                                                                                                             
echo $'\n'                                                                                                                       
                                                                                                                                 
echo "START TADD"                                                                                                             
echo "----------"                                                                                                             
${JAVA_BIN} -Dferris.project.home="${PD}" -jar ${PD}/lib/ferris-tadd-${project.version}.jar  

