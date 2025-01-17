# Crypto Trading Bot

This project is designed to automate trading using Kraken API.

## Features

- **Automated Trading**: Executes trades based on predefined strategies without manual intervention.

## Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Ensure that JDK 21 installed on your machine.
- **Gradle**: This project uses Gradle for build automation.

### Installation

1. **Clone the repository**:

```bash
   git clone https://github.com/leandrodalbo/crypto-bot.git
   cd crypto-bot
```

2. **Custom configurations**:

```yml
kraken:
  key: abc
  secret: def
```

3. **Run Bot**:

```bash
    ./gradlew bootRun
```
