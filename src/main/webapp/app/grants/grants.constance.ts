export function sliceString(text: string, fontSize: number, numberLine: number, width: number) {
    const rs = [];
    const numberCharacterOfLine = Math.round((width * 2.1) / fontSize);
    let countLine = 1;
    const arrayBeginWord = [];
    let beginLine = 0;
    for (let i = 0; i < text.length; i++) {
        if (text[i] === ' ') {
            const l = i - beginLine;
            const endString = arrayBeginWord[arrayBeginWord.length - 1];
            arrayBeginWord.push(i + 1);
            if (Math.floor(l / numberCharacterOfLine) === 1) {
                countLine++;
                beginLine = endString;
                if (countLine === (numberLine + 1)) {
                    rs.push(text.substring(0, endString - 3) + '...');
                    rs.push(countLine - 1);
                    return rs;
                }
            }
        }
    }

    rs.push(text);
    rs.push(countLine);
    return rs;
}
