# Crashlogger discord

This repository contains the code for the Cozy crashes bot. See the license for more information on how to use.

## Features

* A minecraft crashlogging parsing system
* Minecraft snapshot alerting and tracking tools
* Miscellaneous utilities

# Development Requirements

If you're here to help out, here's what you'll need. Firstly:

* A JDK, **Java 15 or later** - if you need one, try [Adoptium](https://adoptium.net/)
* An IDE suitable for Kotlin **and Gradle** work
	* [IntelliJ IDEA](https://www.jetbrains.com/idea/): Community Edition should be plenty
	* [Eclipse](https://www.eclipse.org/ide/): Install the latest version
	  of [the Kotlin plugin](https://marketplace.eclipse.org/content/kotlin-plugin-eclipse), then go to the `Window`
	  menu, `Preferences`, `Kotlin`, `Compiler` and make sure you set up the `JDK_HOME` and JVM target version
* A Discord bot application, created at [the developer dashboard](https://discord.com/developers/applications). Make
  sure you turn on all the privileged intents - different modes require different intents!

# Setting Up

As a first step, fork this repository, clone your fork, and open it in your IDE, importing the Gradle project. Create
a file named `.env` in the project root (next to files like the `build.gradle.kts`), and fill it out with your bot's
settings. This file should contain `KEY=value` pairs, without a space around the `=` and without added quotes:

```dotenv
# https://discord.com/developers
TOKEN=AAA....

# ID of the server to use for testing
TEST_SERVER=1234...
# You get the idea.
```

# Conventions and Linting

This repository makes use of [detekt](https://detekt.github.io/detekt/), a static analysis tool for Kotlin code. Our
formatting rules are contained within [detekt.yml](detekt.yml), but detekt can't verify everything.

To be specific, proper spacing is important for code readability. If your code is too dense, then we're going to ask
you to fix this problem - so try to bear it in mind. Let's see some examples...

### Bad

```kotlin
override suspend fun unload() {
	super.unload()
	if (::task.isInitialized) {
		task.cancel()
	}
}
```

```kotlin
action {
	val channel = channel.asChannel() as ThreadChannel
	val member = user.asMember(guild!!.id)
	val roles = member.roles.toList().map { it.id }
	if (MODERATOR_ROLES.any { it in roles }) {
		targetMessages.forEach { it.pin("Pinned by ${member.tag}") }
		edit { content = "Messages pinned." }
		return@action
	}
	if (channel.ownerId != user.id && threads.isOwner(channel, user) != true) {
		respond { content = "**Error:** This is not your thread." }
		return@action
	}
	targetMessages.forEach { it.pin("Pinned by ${member.tag}") }
	edit { content = "Messages pinned." }
}
```

```kotlin
action {
	if (this.member?.asMemberOrNull()?.mayManageRole(arguments.role) == true) {
		arguments.targetUser.removeRole(
			arguments.role.id,
			"${this.user.asUserOrNull()?.tag ?: this.user.id} used /team remove"
		)
		respond {
			content = "Successfully removed ${arguments.targetUser.mention} from " +
					"${arguments.role.mention}."
			allowedMentions { }
		}
	} else {
		respond {
			content = "Your team needs to be above ${arguments.role.mention} in order to remove " +
					"anyone from it."
			allowedMentions { }
		}
	}
}
```

### Good

```kotlin
override suspend fun unload() {
	super.unload()

	if (::task.isInitialized) {
		task.cancel()
	}
}
```

```kotlin
action {
	val channel = channel.asChannel() as ThreadChannel
	val member = user.asMember(guild!!.id)
	val roles = member.roles.toList().map { it.id }

	if (MODERATOR_ROLES.any { it in roles }) {
		targetMessages.forEach { it.pin("Pinned by ${member.tag}") }
		edit { content = "Messages pinned." }

		return@action
	}

	if (channel.ownerId != user.id && threads.isOwner(channel, user) != true) {
		respond { content = "**Error:** This is not your thread." }

		return@action
	}

	targetMessages.forEach { it.pin("Pinned by ${member.tag}") }

	edit { content = "Messages pinned." }
}
```

```kotlin
action {
	if (this.member?.asMemberOrNull()?.mayManageRole(arguments.role) == true) {
		arguments.targetUser.removeRole(
			arguments.role.id,

			"${this.user.asUserOrNull()?.tag ?: this.user.id} used /team remove"
		)

		respond {
			content = "Successfully removed ${arguments.targetUser.mention} from " +
					"${arguments.role.mention}."

			allowedMentions { }
		}
	} else {
		respond {
			content = "Your team needs to be above ${arguments.role.mention} in order to remove " +
					"anyone from it."

			allowedMentions { }
		}
	}
}
```

Hopefully these examples help to make things clearer. Group similar types of statements together (variable assignments),
separating them from other types (like function calls). If a statement takes up multiple lines, then it probably needs
to be separated from any other statements. In general, use your best judgement - extra space is better than not enough
space, and detekt will tell you if you go overboard.

## Further Reading

This repository makes use of the Kordex plugin. To learn more about KordEx and how to work with it, [please read the documentation](https://docs.kordex.dev).

For more information on the KordEx Gradle plugin and what you can do with it,
[please read this README](https://github.com/Kord-Extensions/gradle-plugins#kordex-plugin).
