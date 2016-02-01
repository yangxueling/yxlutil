#version 1.3 release
	<dependency>
		<groupId>com.yxlisv</groupId>
		<artifactId>yxl-util</artifactId>
		<version>1.3</version>
	</dependency>

#version 1.3.1 snapshot
	<repositories>
		<repository>
			<id>snapshots-oss</id>
			<url>https://oss.sonatype.org/content/repositories/snapshots</url>
			<releases>
				<enabled>false</enabled>
			</releases>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</repository>
	</repositories>
	<dependency>
		<groupId>com.yxlisv</groupId>
		<artifactId>yxl-util</artifactId>
		<version>1.3.1-SNAPSHOT</version>
	</dependency>