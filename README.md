# STILL BEING DEVELOPED

# Crypto Trading Bot

This project is designed to automate trading using Kraken API.

## Features

- **Automated Trading**: Executes trades based on predefined strategies without manual intervention.
- **Customizable Strategies**: Allows users choose between multiple strategies.


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

2.**Custom configurations**:

```yml
kraken:
  url: https://api.kraken.com
  key: abc
  secret: def
  candlesInterval: 60
  sinceDays: 2
   
```

1. **Run Bot**:

```bash
    ./gradlew bootRun
```
