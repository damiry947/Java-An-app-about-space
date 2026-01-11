import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Game extends JFrame {
    
    // Игровые переменные
    private int health = 100;
    private int hunger = 50;
    private int thirst = 50;
    private int energy = 100;
    private int day = 1;
    private int parts = 0;
    private int maxEnergy = 100;
    private int noHungerDays = 0;
    private int noThirstDays = 0;
    private boolean immortal = false;
    private int immortalDays = 0;
    
    // Новые переменные для саботажа
    private boolean transmitterBroken = false;
    private int daysToFindTransmitter = 0;
    private boolean usedCheatCode = false;
    private boolean dayPassed = false;
    private int actionsDoneToday = 0; // Счетчик действий за день
    
    // Графические элементы
    private JLabel healthLabel;
    private JLabel hungerLabel;
    private JLabel thirstLabel;
    private JLabel energyLabel;
    private JLabel dayLabel;
    private JLabel partsLabel;
    private JLabel statusLabel;
    private JLabel actionsLabel;
    private JTextArea logArea;
    private JTextField cheatField;
    private JButton repairButton;
    private JButton nextDayButton;
    private Random random = new Random();
    
    // Секретные коды
    private final String SECRET_CODE_1 = "64649";
    private final String SECRET_CODE_2 = "1001";
    private final String SECRET_CODE_3 = "777";
    private final String SECRET_CODE_4 = "999";
    private final String SECRET_CODE_5 = "12345";
    private final String SECRET_CODE_6 = "00000";
    
    public Game() {
        // Настройка окна
        setTitle("Космический Выживальщик - Осторожно с читами!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());
        
        // ===== ВЕРХНЯЯ ПАНЕЛЬ СТАТУСА =====
        JPanel statusPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusPanel.setBackground(new Color(40, 40, 60));
        
        healthLabel = createStatusLabel("Здоровье: " + health + "/100", Color.RED);
        hungerLabel = createStatusLabel("Голод: " + hunger + "/100", Color.ORANGE);
        thirstLabel = createStatusLabel("Жажда: " + thirst + "/100", Color.CYAN);
        energyLabel = createStatusLabel("Энергия: " + energy + "/" + maxEnergy, Color.YELLOW);
        dayLabel = createStatusLabel("День: " + day, Color.WHITE);
        partsLabel = createStatusLabel("Запчасти: " + parts + "/10", Color.GREEN);
        statusLabel = createStatusLabel("Передатчик: РАБОТАЕТ", Color.LIGHT_GRAY);
        actionsLabel = createStatusLabel("Действий сегодня: 0", Color.PINK);
        
        statusPanel.add(healthLabel);
        statusPanel.add(hungerLabel);
        statusPanel.add(thirstLabel);
        statusPanel.add(energyLabel);
        statusPanel.add(dayLabel);
        statusPanel.add(partsLabel);
        statusPanel.add(statusLabel);
        statusPanel.add(actionsLabel);
        
        // ===== ЦЕНТР: ЛОГ СОБЫТИЙ =====
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(20, 20, 30));
        logArea.setForeground(Color.WHITE);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setText("==========================================\n" +
                       "   КОСМИЧЕСКИЙ ВЫЖИВАЛЬЩИК\n" +
                       "   Твой корабль разбился на\n" +
                       "   неизвестной планете...\n" +
                       "   Цель: собрать 10 запчастей\n" +
                       "   и починить передатчик!\n" +
                       "==========================================\n\n" +
                       "ПРАВИЛА:\n" +
                       "1. День проходит ТОЛЬКО при нажатии\n" +
                       "   кнопки 'Следующий день'\n" +
                       "2. НО: 'Искать передатчик' тратит день!\n" +
                       "3. За день можно сделать сколько угодно\n" +
                       "   действий, пока есть энергия\n" +
                       "4. Использование читов опасно!\n\n");
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("События"));
        
        // ===== ЛЕВАЯ ПАНЕЛЬ: ДЕЙСТВИЯ =====
        JPanel leftPanel = new JPanel(new GridLayout(7, 1, 5, 5));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.setBackground(new Color(50, 50, 70));
        
        // Создаем кнопки действий
        JButton searchFoodButton = createActionButton("Искать еду (-20 энергии)", 
            new Color(80, 180, 80), e -> searchFood());
        
        JButton searchWaterButton = createActionButton("Искать воду (-15 энергии)", 
            new Color(80, 130, 220), e -> searchWater());
        
        JButton restButton = createActionButton("Отдыхать", 
            new Color(180, 180, 80), e -> rest());
        
        JButton exploreButton = createActionButton("Исследовать (-30 энергии)", 
            new Color(130, 80, 180), e -> explore());
        
        // Кнопка для ремонта/поиска передатчика (меняется!)
        repairButton = createActionButton("Чинить передатчик (-40 энергии)", 
            new Color(220, 120, 80), e -> repairTransmitter());
        
        JButton inventoryButton = createActionButton("Показать инвентарь", 
            new Color(180, 80, 180), e -> showInventory());
        
        JButton cheatInfoButton = createActionButton("Инфо о читах", 
            new Color(100, 100, 100), e -> showCheatInfo());
        
        leftPanel.add(searchFoodButton);
        leftPanel.add(searchWaterButton);
        leftPanel.add(restButton);
        leftPanel.add(exploreButton);
        leftPanel.add(repairButton);
        leftPanel.add(inventoryButton);
        leftPanel.add(cheatInfoButton);
        
        // ===== ПРАВАЯ ПАНЕЛЬ: СЛЕДУЮЩИЙ ДЕНЬ И КОДЫ =====
        JPanel rightPanel = new JPanel(new BorderLayout(5, 10));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.setBackground(new Color(50, 50, 70));
        
        // Панель секретных кодов
        JPanel cheatPanel = new JPanel(new BorderLayout(5, 0));
        cheatPanel.setBackground(new Color(60, 60, 80));
        cheatPanel.setBorder(BorderFactory.createTitledBorder("Секретный код (риск!)"));
        
        cheatField = new JTextField();
        cheatField.setFont(new Font("Consolas", Font.BOLD, 12));
        cheatField.setBackground(Color.BLACK);
        cheatField.setForeground(Color.GREEN);
        cheatField.setCaretColor(Color.GREEN);
        cheatField.addActionListener(e -> processCheatCode());
        
        JButton cheatButton = new JButton("Активировать");
        cheatButton.setBackground(new Color(180, 80, 80));
        cheatButton.setForeground(Color.WHITE);
        cheatButton.setFont(new Font("Arial", Font.BOLD, 11));
        cheatButton.addActionListener(e -> processCheatCode());
        
        JLabel warningLabel = new JLabel("Читы = взрыв передатчика!");
        warningLabel.setForeground(Color.RED);
        warningLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        
        cheatPanel.add(warningLabel, BorderLayout.NORTH);
        cheatPanel.add(cheatField, BorderLayout.CENTER);
        cheatPanel.add(cheatButton, BorderLayout.EAST);
        
        // Кнопка следующего дня
        nextDayButton = new JButton("Следующий день");
        nextDayButton.setBackground(new Color(80, 80, 200));
        nextDayButton.setForeground(Color.WHITE);
        nextDayButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextDayButton.setPreferredSize(new Dimension(180, 50));
        nextDayButton.addActionListener(e -> nextDay());
        
        JPanel nextDayPanel = new JPanel();
        nextDayPanel.setBackground(new Color(50, 50, 70));
        nextDayPanel.add(nextDayButton);
        
        rightPanel.add(cheatPanel, BorderLayout.NORTH);
        rightPanel.add(nextDayPanel, BorderLayout.SOUTH);
        
        // ===== НИЖНЯЯ ПАНЕЛЬ: ИНФО =====
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(30, 30, 40));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel infoLabel = new JLabel("ВАЖНО: 'Искать передатчик' тратит целый день! | Обычные действия не тратят дни.");
        infoLabel.setForeground(Color.YELLOW);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 11));
        bottomPanel.add(infoLabel);
        
        // ===== СОБИРАЕМ ВСЕ ВМЕСТЕ =====
        add(statusPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Центрируем окно
        setLocationRelativeTo(null);
    }
    
    // Создание метки статуса
    private JLabel createStatusLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(color);
        label.setOpaque(true);
        label.setBackground(new Color(30, 30, 30));
        label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        return label;
    }
    
    // Создание кнопки действия
    private JButton createActionButton(String text, Color color, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setFocusPainted(false);
        button.setMargin(new Insets(8, 5, 8, 5));
        button.addActionListener(action);
        return button;
    }
    
    // Добавление сообщения в лог
    private void addLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    // Обновление интерфейса
    private void updateUI() {
        healthLabel.setText("Здоровье: " + health + "/100");
        hungerLabel.setText("Голод: " + hunger + "/100");
        thirstLabel.setText("Жажда: " + thirst + "/100");
        energyLabel.setText("Энергия: " + energy + "/" + maxEnergy);
        dayLabel.setText("День: " + day);
        partsLabel.setText("Запчасти: " + parts + "/10");
        actionsLabel.setText("Действий сегодня: " + actionsDoneToday);
        
        // Обновляем статус передатчика
        if (transmitterBroken) {
            statusLabel.setText("Передатчик: СЛОМАН!");
            statusLabel.setForeground(Color.RED);
            repairButton.setText("Искать передатчик (" + daysToFindTransmitter + " дн.)");
            repairButton.setBackground(new Color(180, 80, 80));
            repairButton.setEnabled(true);
        } else {
            statusLabel.setText("Передатчик: работает");
            statusLabel.setForeground(Color.GREEN);
            repairButton.setText("Чинить передатчик (-40 энергии)");
            repairButton.setBackground(new Color(220, 120, 80));
            repairButton.setEnabled(energy >= 40);
        }
        
        // Обновляем кнопку следующего дня
        if (dayPassed) {
            nextDayButton.setBackground(new Color(200, 100, 100));
            nextDayButton.setText("Следующий день (уже был!)");
            nextDayButton.setEnabled(false);
        } else {
            nextDayButton.setBackground(new Color(80, 80, 200));
            nextDayButton.setText("Следующий день");
            nextDayButton.setEnabled(true);
        }
        
        // Проверка условий
        if (health <= 0 && !immortal) {
            gameOver("ТЫ ПОГИБ! Прожил " + day + " дней.");
        } else if (parts >= 10 && !transmitterBroken) {
            gameOver("ПОБЕДА! Ты починил корабль и улетел!");
        }
    }
    
    // Информация о читах
    private void showCheatInfo() {
        addLog("\n=== СЕКРЕТНАЯ ИНФОРМАЦИЯ ===");
        addLog("Чит-коды дают преимущества, НО:");
        addLog("1. Если использовать читы ДО победы,");
        addLog("   передатчик может взорваться!");
        addLog("2. Взорванный передатчик нужно");
        addLog("   искать 8 дней (каждый поиск = 1 день)");
        addLog("3. Будь осторожен с кодами!");
        addLog("========================");
    }
    
    // Обработка секретных кодов
    private void processCheatCode() {
        String code = cheatField.getText().trim();
        cheatField.setText("");
        
        if (code.isEmpty()) return;
        
        // Помечаем, что использовали чит
        usedCheatCode = true;
        addLog("[День " + day + "] Ввод секретного кода: " + code);
        
        switch (code) {
            case SECRET_CODE_1: // 64649
                noHungerDays = 2;
                noThirstDays = 2;
                maxEnergy = 250;
                energy = maxEnergy;
                addLog("[ЧИТ] Активирована СУПЕР-СИЛА!");
                addLog("[ЧИТ] 2 дня без голода и жажды!");
                addLog("[ЧИТ] Макс. энергия: 250!");
                break;
                
            case SECRET_CODE_2: // 1001
                health = 100;
                hunger = 0;
                thirst = 0;
                energy = maxEnergy;
                addLog("[ЧИТ] Полное восстановление!");
                addLog("[ЧИТ] Передатчик стал нестабильным...");
                break;
                
            case SECRET_CODE_3: // 777
                parts += 5;
                addLog("[ЧИТ] +5 запчастей!");
                addLog("[ЧИТ] Запчасти подозрительного качества...");
                break;
                
            case SECRET_CODE_4: // 999
                immortal = true;
                immortalDays = 1;
                addLog("[ЧИТ] Бессмертие на 1 день!");
                addLog("[ЧИТ] Энергия передатчика искажена...");
                break;
                
            case SECRET_CODE_5: // 12345
                health = 100;
                hunger = 0;
                thirst = 0;
                maxEnergy = 500;
                energy = maxEnergy;
                parts = 999;
                noHungerDays = 999;
                noThirstDays = 999;
                addLog("[ЧИТ] РЕЖИМ БОГА АКТИВИРОВАН!");
                addLog("[ЧИТ] Передатчик перегружен...");
                break;
                
            case SECRET_CODE_6: // 00000
                health = 100;
                hunger = 50;
                thirst = 50;
                maxEnergy = 100;
                energy = 100;
                parts = 0;
                day = 1;
                noHungerDays = 0;
                noThirstDays = 0;
                immortal = false;
                immortalDays = 0;
                transmitterBroken = false;
                daysToFindTransmitter = 0;
                usedCheatCode = false;
                dayPassed = false;
                actionsDoneToday = 0;
                addLog("[ЧИТ] Игра сброшена!");
                addLog("[ЧИТ] Все эффекты читов сняты.");
                break;
                
            default:
                addLog("Неизвестный код: " + code);
                usedCheatCode = false;
                return;
        }
        
        updateUI();
    }
    
    // Ремонт или поиск передатчика (ВАЖНО: поиск тратит день!)
    private void repairTransmitter() {
        actionsDoneToday++;
        
        if (transmitterBroken) {
            // Ищем сломанный передатчик - это тратит целый день!
            if (daysToFindTransmitter > 0) {
                addLog("\n========== ПОИСК ПЕРЕДАТЧИКА ==========");
                addLog("[День " + day + "] ЦЕЛЫЙ ДЕНЬ ищешь сломанный передатчик...");
                
                // Проходит день
                passDayEffects();
                day++;
                daysToFindTransmitter--;
                dayPassed = true;
                actionsDoneToday = 0;
                
                if (daysToFindTransmitter == 0) {
                    transmitterBroken = false;
                    addLog("[День " + day + "] УРА! Нашел сломанный передатчик!");
                    addLog("[День " + day + "] Теперь можно починить его.");
                } else {
                    addLog("[День " + day + "] Осталось дней поиска: " + daysToFindTransmitter);
                }
                addLog("==========================================");
            }
        } else {
            // Чиним передатчик (не тратит день)
            addLog("[День " + day + "] Пытаешься починить передатчик...");
            
            if (energy >= 40) {
                energy -= 40;
                
                // Проверяем, использовались ли читы
                if (usedCheatCode && random.nextInt(100) < 80) { // 80% шанс взрыва
                    transmitterBroken = true;
                    daysToFindTransmitter = 8;
                    addLog("[День " + day + "] ПЫЩЩЩЩЩЩЩЩЩЩЩЩЩЩЩЩЩЩЩ!");
                    addLog("[День " + day + "] ПЕРЕДАТЧИК ВЗОРВАЛСЯ!");
                    addLog("[День " + day + "] Ты использовал читы!");
                    addLog("[День " + day + "] Ищи передатчик " + daysToFindTransmitter + " дней!");
                    addLog("[ВАЖНО] Каждый поиск = 1 полный день!");
                } else {
                    int progress = random.nextInt(3) + 1;
                    parts += progress;
                    addLog("[День " + day + "] Успех! +" + progress + " запчастей");
                    addLog("[День " + day + "] Всего запчастей: " + parts + "/10");
                    
                    // Небольшой шанс взрыва даже без читов
                    if (random.nextInt(100) < 10) { // 10% шанс
                        transmitterBroken = true;
                        daysToFindTransmitter = 3;
                        addLog("[День " + day + "] ОЙ! Передатчик сломался!");
                        addLog("[День " + day + "] Ищи его " + daysToFindTransmitter + " дня.");
                    }
                }
            } else {
                addLog("[День " + day + "] Слишком устал для ремонта! Нужно 40 энергии.");
            }
        }
        updateUI();
    }
    
    // Эффекты при прохождении дня (для поиска передатчика)
    private void passDayEffects() {
        // Уменьшаем дни защиты
        if (noHungerDays > 0) {
            noHungerDays--;
        }
        
        if (noThirstDays > 0) {
            noThirstDays--;
        }
        
        if (immortal && immortalDays > 0) {
            immortalDays--;
            if (immortalDays == 0) {
                immortal = false;
            }
        }
        
        // Если нет защиты - увеличиваем голод/жажду
        if (noHungerDays == 0) {
            hunger = Math.min(100, hunger + 15);
        }
        
        if (noThirstDays == 0) {
            thirst = Math.min(100, thirst + 12);
        }
        
        // Эффекты от голода/жажды
        if (hunger > 80 && noHungerDays == 0 && !immortal) {
            health -= 5;
        }
        if (thirst > 80 && noThirstDays == 0 && !immortal) {
            health -= 5;
        }
        
        // Восстановление при низких показателях
        if (hunger < 30 && thirst < 30 && noHungerDays == 0 && noThirstDays == 0) {
            health = Math.min(100, health + 3);
        }
        
        // Небольшое восстановление энергии каждый день
        energy = Math.min(maxEnergy, energy + 10);
    }
    
    // Конец игры
    private void gameOver(String message) {
        addLog("\n==========================================");
        addLog(message);
        addLog("==========================================\n");
        
        // Отключаем все кнопки
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                disableButtons((JPanel)comp);
            }
        }
    }
    
    private void disableButtons(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JButton) {
                ((JButton)comp).setEnabled(false);
            } else if (comp instanceof JPanel) {
                disableButtons((JPanel)comp);
            }
        }
    }
    
    // ========== ОСНОВНЫЕ ДЕЙСТВИЯ (НЕ МЕНЯЮТ ДЕНЬ!) ==========
    
    private void searchFood() {
        actionsDoneToday++;
        addLog("[День " + day + "] Ищешь еду...");
        
        if (energy >= 20) {
            energy -= 20;
            int luck = random.nextInt(100);
            
            if (luck > 60) {
                int food = random.nextInt(30) + 10;
                if (noHungerDays == 0) hunger = Math.max(0, hunger - food);
                addLog("[День " + day + "] Нашел " + food + " единиц еды!");
            } else if (luck > 30) {
                addLog("[День " + day + "] Ничего не нашел...");
            } else {
                addLog("[День " + day + "] На тебя напало местное существо!");
                if (!immortal) health -= random.nextInt(20) + 10;
                addLog("[День " + day + "] Здоровье: " + health);
            }
        } else {
            addLog("[День " + day + "] Слишком устал для поисков!");
        }
        updateUI();
    }
    
    private void searchWater() {
        actionsDoneToday++;
        addLog("[День " + day + "] Ищешь воду...");
        
        if (energy >= 15) {
            energy -= 15;
            int luck = random.nextInt(100);
            
            if (luck > 50) {
                int water = random.nextInt(25) + 10;
                if (noThirstDays == 0) thirst = Math.max(0, thirst - water);
                addLog("[День " + day + "] Нашел " + water + " единиц воды!");
            } else {
                addLog("[День " + day + "] Воды не найдено...");
            }
        } else {
            addLog("[День " + day + "] Слишком устал!");
        }
        updateUI();
    }
    
    private void rest() {
        actionsDoneToday++;
        addLog("[День " + day + "] Отдыхаешь...");
        
        int recovery = random.nextInt(30) + 20;
        energy = Math.min(maxEnergy, energy + recovery);
        
        if (noHungerDays == 0) hunger = Math.min(100, hunger + 10);
        if (noThirstDays == 0) thirst = Math.min(100, thirst + 8);
        
        addLog("[День " + day + "] Восстановил " + recovery + " энергии");
        addLog("[День " + day + "] Энергия: " + energy + "/" + maxEnergy);
        updateUI();
    }
    
    private void explore() {
        actionsDoneToday++;
        addLog("[День " + day + "] Исследуешь планету...");
        
        if (energy >= 30) {
            energy -= 30;
            int event = random.nextInt(100);
            
            if (event > 80) {
                int foundParts = random.nextInt(3) + 1;
                parts += foundParts;
                addLog("[День " + day + "] Нашел обломки корабля!");
                addLog("[День " + day + "] +" + foundParts + " запчастей");
                addLog("[День " + day + "] Всего запчастей: " + parts + "/10");
            } else if (event > 60) {
                addLog("[День " + day + "] Обнаружен странный артефакт...");
            } else if (event > 30) {
                addLog("[День " + day + "] Ничего интересного...");
            } else {
                addLog("[День " + day + "] Попал в песчаную бурю!");
                if (!immortal) health -= random.nextInt(15);
                addLog("[День " + day + "] Здоровье: " + health);
            }
        } else {
            addLog("[День " + day + "] Нужно больше энергии! Нужно 30.");
        }
        updateUI();
    }
    
    private void showInventory() {
        actionsDoneToday++;
        addLog("[День " + day + "] Смотришь инвентарь...");
        
        String inventory = "\n=== ИНВЕНТАРЬ (День " + day + ") ===" +
                          "\n• Аптечка: 1 шт." +
                          "\n• Запчасти: " + parts + "/10 шт." +
                          "\n• Батареи: 2 шт." +
                          "\n• Еда: " + (100 - hunger) + " ед." +
                          "\n• Вода: " + (100 - thirst) + " ед.";
        
        if (noHungerDays > 0) {
            inventory += "\n• Защита от голода: " + noHungerDays + " дн.";
        }
        if (noThirstDays > 0) {
            inventory += "\n• Защита от жажды: " + noThirstDays + " дн.";
        }
        if (immortal) {
            inventory += "\n• Бессмертие: " + immortalDays + " дн.";
        }
        if (maxEnergy > 100) {
            inventory += "\n• Усиленная энергия: " + maxEnergy;
        }
        if (transmitterBroken) {
            inventory += "\n• Передатчик: СЛОМАН";
            inventory += "\n• Дней на поиск: " + daysToFindTransmitter;
        }
        if (usedCheatCode) {
            inventory += "\n• Использованы читы: ДА (риск!)";
        }
        
        inventory += "\n• Энергия: " + energy + "/" + maxEnergy +
                    "\n• Здоровье: " + health + "/100" +
                    "\n• Действий сегодня: " + actionsDoneToday +
                    "\n==================";
        addLog(inventory);
    }
    
    // СЛЕДУЮЩИЙ ДЕНЬ (для обычных действий)
    private void nextDay() {
        if (dayPassed) {
            addLog("[ВНИМАНИЕ] День " + day + " уже прошел!");
            addLog("[ВНИМАНИЕ] Сделай все действия перед сменой дня!");
            return;
        }
        
        addLog("\n========== НАСТУПИЛ ДЕНЬ " + (day + 1) + " ==========");
        
        // Проходят сутки
        passDayEffects();
        day++;
        dayPassed = true;
        actionsDoneToday = 0;
        
        // Сообщения о защите
        if (noHungerDays > 0) {
            addLog("[День " + day + "] Защита от голода: " + noHungerDays + " дн. осталось");
        }
        if (noThirstDays > 0) {
            addLog("[День " + day + "] Защита от жажды: " + noThirstDays + " дн. осталось");
        }
        if (immortal) {
            addLog("[День " + day + "] Бессмертие: " + immortalDays + " дн. осталось");
        }
        
        addLog("[День " + day + "] Голод: " + hunger + "/100");
        addLog("[День " + day + "] Жажда: " + thirst + "/100");
        addLog("[День " + day + "] Энергия: " + energy + "/" + maxEnergy);
        addLog("[День " + day + "] Здоровье: " + health + "/100");
        addLog("==========================================");
        
        updateUI();
    }
    
    // Главный метод
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Game game = new Game();
                game.setVisible(true);
            }
        });
    }
}