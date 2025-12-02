plugins {
	id("mod-platform")
	id("net.neoforged.moddev.legacyforge")
}

repositories {
	maven("https://maven.figuramc.org/releases") { name = "Figura Releases" }
	maven("https://maven.figuramc.org/snapshots") { name = "Figura Snapshots" }
	maven("https://jitpack.io")
}

platform {
	loader = "forge"
	dependencies {
		required("minecraft") {
			forgeVersionRange = "[${prop("deps.minecraft")}]"
		}
		required("forge") {
			forgeVersionRange = "[1,)"
		}
	}
}

legacyForge {
	version = property("deps.forge") as String
	validateAccessTransformers = true

	if (hasProperty("deps.parchment")) parchment {
		val (mc, ver) = (property("deps.parchment") as String).split(':')
		mappingsVersion = ver
		minecraftVersion = mc
	}

	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "Forge Client (${stonecutter.active?.version})"
			programArgument("--username=Dev")
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "Forge Server (${stonecutter.active?.version})"
		}
	}

	mods {
		register(property("mod.id") as String) {
			sourceSet(sourceSets["main"])
		}
	}
	sourceSets["main"].resources.srcDir("${rootDir}/versions/datagen/${stonecutter.current.version.split("-")[0]}/src/main/generated")
}

dependencies {
	compileOnly("com.github.FiguraMC.luaJ:luaj-core:${prop("deps.luaj")}-figura")
	compileOnly("com.github.FiguraMC.luaJ:luaj-jse:${prop("deps.luaj")}-figura")
	compileOnly("com.neovisionaries:nv-websocket-client:${prop("deps.nv_websocket")}")
	modImplementation("org.figuramc:figura-forge:${prop("deps.figura")}+${prop("deps.minecraft")}")
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}
