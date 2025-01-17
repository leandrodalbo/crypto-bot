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

### 1 hour candles conf
```yml
bot:
  validationCron: "0 */15 * * * *"
  newTradeCron: "0 */30 * * * *"
  
kraken:
  key: abc
  secret: def
  candlesInterval: 60
  candlesSince: 5
  candlesSinceUnit: DAY

operation:
  minutesLimit: 90
  stop: 0.006
  profit: 0.018
  currency: USDZ
  notBelow: 500
```

### 15 min candles conf
```yml
bot:
  validationCron: "0 */3 * * * *"
  newTradeCron: "0 */15 * * * *"
  
kraken:
  key: abc
  secret: def
  candlesInterval: 15
  candlesSince: 2
  candlesSinceUnit: DAY

operation:
  minutesLimit: 60
  stop: 0.006
  profit: 0.018
  currency: USDZ
  notBelow: 500
```

3. **Run Bot**:

```bash
    ./gradlew bootRun
```
