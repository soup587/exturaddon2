import org.codehaus.groovy.control.messages.WarningMessage

plugins {
	id("mod-platform")
	id("fabric-loom")
}

repositories {
	maven("https://maven.figuramc.org/releases") { name = "Figura Releases" }
	maven("https://maven.figuramc.org/snapshots") { name = "Figura Snapshots" }
	maven("https://jitpack.io")
}

platform {
	loader = "fabric"
	dependencies {
		required("minecraft") {
			versionRange = prop("deps.minecraft")
		}
		required("fabric-api") {
			slug("fabric-api")
			versionRange = ">=${prop("deps.fabric-api")}"
		}
		required("fabricloader") {
			versionRange = ">=${libs.fabric.loader.get().version}"
		}
		required("figura") {}
	}
}

loom {
	accessWidenerPath = rootProject.file("src/main/resources/aw/${stonecutter.current.version}.accesswidener")
	runs.named("client") {
		client()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "client"
		programArgs("--username=Dev")
		configName = "Fabric Client"
	}
	runs.named("server") {
		server()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "server"
		configName = "Fabric Server"
	}
}

fabricApi {
	configureDataGeneration() {
		outputDirectory = file("${rootDir}/versions/datagen/${stonecutter.current.version.split("-")[0]}/src/main/generated")
		client = true
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
	mappings(
		loom.layered {
			officialMojangMappings()
			if (hasProperty("deps.parchment")) parchment("org.parchmentmc.data:parchment-${prop("deps.parchment")}@zip")
		})
	modImplementation(libs.fabric.loader)
	implementation(libs.moulberry.mixinconstraints)
	include(libs.moulberry.mixinconstraints)
	modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric-api")}")

	implementation("com.github.FiguraMC.luaJ:luaj-core:${prop("deps.luaj")}-figura")
	implementation("com.github.FiguraMC.luaJ:luaj-jse:${prop("deps.luaj")}-figura")
	implementation("com.neovisionaries:nv-websocket-client:${prop("deps.nv_websocket")}")
	if(!prop("deps.figura_version").isEmpty()){
		// This is to allow compiling against versions of Figura that haven't released yet, I think
		modImplementation("org.figuramc:figura-fabric:${prop("deps.figura_version")}")
	}else{
		modImplementation("org.figuramc:figura-fabric:${prop("deps.figura")}+${prop("deps.minecraft")}")

	}
}
