# Netbeans

## Set JDK 11

### GNU/Linux
In /usr/lib/apache-netbeans/etc folder, edit the netbeans.conf file, find and add the following:
- Uncomment
netbeans_jdkhome="/path/to/jdk"
- Set JDK 11 path
netbeans_jdkhome="/usr/lib/jvm/java-11-openjdk"

### Windows
In C:\Program Files\NetBeans-21\netbeans\etc folder, edit the netbeans.conf file, find and add the following:
- Uncomment
netbeans_jdkhome="/path/to/jdk"
- Set JDK 11 path
netbeans_jdkhome="C:\Java\LibericaJDK-11"