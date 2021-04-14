const puppeteer = require('puppeteer');

(async () => {
    const browser = await puppeteer.launch({ headless: true });
    const page = await browser.newPage();
    await page.goto('https://www.brasilbandalarga.com.br/bbl/');
    await page.waitForFunction('document.querySelector(\'#btnIniciar\').innerText === \'INICIAR TESTE\'', { timeout: 0 });

    await page.click('#btnIniciar');

    await page.waitForFunction('document.querySelector(\'#btnIniciar\').innerText === \'INICIAR NOVO TESTE\'', { timeout: 0 });

    // Esperar 5 segundos
    await new Promise((resolve, reject) => setTimeout(resolve, 2000));

    const dataAtual = new Date();
    const dataFormatada = dataAtual.toLocaleString().replace(/[:]/g, '-');

    await page.pdf({path: 'teste_bbl_'+dataFormatada+'.pdf'});

    await browser.close();
})();