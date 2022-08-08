export default function declOfNum(number, words) {
    const condition = (number % 100 > 4 && number % 100 < 20);
    const value1 = 2;
    const value2 = [2, 0, 1, 1, 1, 2][(number % 10 < 5) ? number % 10 : 5];
    const index = condition ? value1 : value2;
    return words[index];
}