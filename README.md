# Ana Liza Crash — Landing Page v3

Melhorias da v3:
- Fonte Orbitron, próxima do primeiro modelo.
- Ícone do app restaurado no menu e no hero.
- Fundo sólido premium com brilho em CSS, sem aparência de baixa resolução.
- Ícones de recursos redesenhados em SVG/CSS: radar, score 7.5, cadeado, gráfico, alvo e pulso.
- Botões finais centralizados.
- Radar animado no hero e no bloco de vídeo.
- Indicador 91%.

## Publicar no GitHub Pages
1. Crie um repositório no GitHub.
2. Envie `index.html`, `style.css`, `script.js` e a pasta `assets` para a raiz.
3. Vá em Settings > Pages.
4. Em Build and deployment, escolha Deploy from a branch.
5. Selecione `main` e `/root`.

## Ajustar links
No `index.html`, substitua:
- `https://t.me/` pelo link do Telegram.
- `mailto:ico@terra.com.br` por outro contato, se quiser.

## Inserir vídeo
Substitua o bloco `.video-box` por:
```html
<video controls poster="assets/capa-video.jpg">
  <source src="assets/video-app.mp4" type="video/mp4">
</video>
```
