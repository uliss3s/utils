/*
Código utilitário para realizar o teste banda larga do site https://beta.simet.nic.br/ e salvá-lo em PDF.
O primeiro teste é no servidor mais próximo detectado pelo site. O segundo é feito em São Paulo.

tag id localidade: dropdownLocalitySelectorButton
 */

const puppeteer = require('puppeteer');

(async () => {
    const browser = await puppeteer.launch();
    const page = await browser.newPage();
    await page.goto('https://beta.simet.nic.br');
    await page.waitForFunction('document.querySelectorAll(\'div[class=lds-dual-ring]\').length == 0', { timeout: 0 });

    await new Promise((resolve, reject) => setTimeout(resolve, 2000));

    const dataAtual = new Date();
    const dataFormatada = dataAtual.toLocaleString().replace(/[:]/g, '-');

    await page.pdf({path: 'teste_simet_'+dataFormatada+'.pdf'});

    await page.click('#dropdownLocalitySelectorButton', { delay: 1000 });

    const elArray = await page.$$('a.dropdown-item');

    if (typeof elArray !== 'undefined') {
        for (let i = 0; i < elArray.length; i++) {
            const texto = await page.evaluate(element => element.textContent, elArray[i]);
            if (texto === 'São Paulo - SP') {
                await elArray[i].click({ delay: 1000 });
            }
        }
    }

    await page.waitForFunction('document.querySelectorAll(\'div[class=lds-dual-ring]\').length == 0', { timeout: 0 });

    await new Promise((resolve, reject) => setTimeout(resolve, 2000));

    const dataAtual2 = new Date();
    const dataFormatada2 = dataAtual2.toLocaleString().replace(/[:]/g, '-');

    await page.pdf({path: 'teste_simet_saopaulo_'+dataFormatada2+'.pdf'});

    await browser.close();
})();
