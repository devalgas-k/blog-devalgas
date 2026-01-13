import { defineComponent } from 'vue';
import { useI18n } from 'vue-i18n';

/**
 * Composant d’icônes de réseaux sociaux.
 * Expose une liste de liens vers les plateformes et les libellés i18n.
 */
export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'SocialMedia',
  setup() {
    const t$ = useI18n().t;

    const socialLinks = [
      {
        name: 'linkedin',
        url: LINKEDIN_URL,
        icon: 'pi pi-linkedin',
        ariaLabel: 'LinkedIn',
      },
      {
        name: 'twitter',
        url: TWITTER_URL,
        icon: 'pi pi-twitter',
        ariaLabel: 'Twitter',
      },
      {
        name: 'github',
        url: GITHUB_URL,
        icon: 'pi pi-github',
        ariaLabel: 'GitHub',
      },
      {
        name: 'medium',
        url: MEDIUM_URL,
        icon: null,
        ariaLabel: 'Medium',
      },
    ];

    return { t$, socialLinks };
  },
});
