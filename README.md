# VistA Adapter for CareWeb Framework
This project allows the CareWeb Framework to run on top of the Veterans Health Administration's [VistA EMR](http://www.ehealth.va.gov/VistA.asp).  It is pre-configured to run on the  [WorldVistA WVEHR database](http://worldvista.org/Software_Download)  (not provided), but can be run on other VistA distros as well.

The project includes the NETSERV RPC broker and integrates broker-based security with
the Spring Security framework.

The project also includes several clinical plugins, some fully functional while others are in the early
stages of development.

Installation:

1. Using KIDS, install the provided build <b>kids/cwf-vista-1.0.kid</b>.
2. At the M command line, execute <b>D STARTALL^RGNETTCP</b> to start the broker on port 9300.
3. If TaskMan is not running, it may be started from the M command line by entering <b>D ^ZTMB</b>.

Running:

1. Using Maven, build and install the separate <b>carewebframework-core</b> and <b>carewebframework-icons</b> projects.
2. In the <b>carewebframework-vista</b> project under the <b>org.carewebframework.vista.web.impl.wvehr</b> artifact, edit the <b>cwf.properties</b> file to reflect your broker and user authentication settings.
3. Using Maven, build the <b>carewebframework-vista</b> project.  A war file will be created under the <b>target</b> folder of the <b>org.carewebframework.vista.web.impl.wvehr</b> artifact.
4. Deploy and run the created war file using Tomcat or other servlet container.

Additional configuration:

1. To access the design mode feature of the CWF, assign the <b>RGCWF DESIGNER</b> security key.
2. To enable patient selection access, assign the <b>RGCWPT PATIENT SELECT</b> security key.
